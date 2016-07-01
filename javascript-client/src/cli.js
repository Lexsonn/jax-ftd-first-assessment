import net from 'net'
import chalk from 'chalk'
import vorpal from 'vorpal'
import base64 from 'base64-js'
import {hash, compare} from './hashing'

const { User } = require('./user')
const { FileD, filePromise, fileWritePromise } = require('./fileD')

const cli = vorpal()
let sessionID = ''
let currentUser
let server

let port = 667
let address = '127.0.0.1'

const DEFAULT_DELIMITER = 'ftd-auth:'

/**
 *  Parses the response made by the server, whose message always contains the *
 *  delimiter to signify message type.
 *  @param args - arguments entered in by command line
 *  @param data - JSON data object passed in through the socket by the server
 *  @param callback - callback function
 */
function parseServerResponse (args, data, callback) {
  let { response } = JSON.parse(data)
  let arr = /^\*(.*)\*(.*)$/.exec(response.message)
  let type = arr[1]
  let msg = arr[2]
  switch (type) {
    case 'error':
      cli.log(chalk.bold.red(`${msg}`))
      server.end()
      callback()
      break
    case 'user':
      cli.log(chalk.green(`${msg}`))
      server.end()
      callback()
      break
    case 'loginError':
      cli.log(chalk.bold.red(`${msg}`))
      sessionID = ''
      cli.delimiter(`${DEFAULT_DELIMITER}`)
      server.end()
      callback()
      break
    case 'checkPass':
      currentUser = JSON.parse(response.data.replace('\\', ''))
      currentUser = currentUser.user
      compare(args.password + '', currentUser.password)
        .then((successFlag) => successFlag
          ? server.write(`${JSON.stringify({clientMessage: {message: 'success', data: `success`}})}\n`)
          : server.write(`${JSON.stringify({clientMessage: {message: 'Daimen is smart', data: `Daimen is awesome`}})}\n`)
        )
      break
    case 'login':
      cli.log(chalk.green(`${msg}`))
      sessionID = '*' + response.data.value
      cli.delimiter(`${currentUser.username}:`)
      server.end()
      callback()
      break
    case 'filelistSuccess':
      cli.log(chalk.bold.bgGreen(`${msg}`))
      cli.log(chalk.green(response.data.value))
      server.end()
      callback()
      break
    case 'uploadSuccess':
      cli.log(chalk.green(`${msg}`))
      server.end()
      callback()
      break
    case 'downloadSuccess':
      cli.log(chalk.green(`${msg}`))
      let fileD = JSON.parse(response.data.replace('\\', ''))
      fileD = fileD.fileD
      let filepath = fileD.filepath
      if ('local_filepath' in args) {
        filepath = args.local_filepath
      }
      cli.log(chalk.bold(`writing to filepath: ${filepath}`))
      fileWritePromise(filepath, new Buffer(base64.toByteArray(fileD.file)))
        .then((successFlag) => successFlag
            ? cli.log(chalk.green(`File succesfully written`))
            : cli.log(chalk.red(`File ${filepath} , has not been written`))
          )
        .catch((err) => cli.log(chalk.bold.red(`Error writing to file ${err}`)))
      // this.log(JSON.stringify(fileD))
      server.end()
      callback()
      break
    default:
      cli.log(`type: ${type} message: ${msg}`)
      server.end()
      callback()
  }
}

/**
 *  Processes the various events handled by the server socket.
 *  @param server - the server socket
 *  @param args - the arguments passed in by command line
 *  @param callback - callback function
 */
function processServer (server, args, callback) {
  server.on('data', (data) => {
    parseServerResponse(args, data, callback)
  })

  server.on('end', () => {
    cli.log(chalk.red('Server has been disconnected.'))
  })

  server.on('error', () => {
    // cli.log(chalk.bold.red(`Serverside error encountered`))
  })
}

cli
  .delimiter(`${DEFAULT_DELIMITER}`)

const connect = cli.command('connect <port> <address>')
connect
    .description(`Sets connection to the given port and address`)
    .alias('conn', 'c')
    .action(function (args, callback) {
      address = args.address
      port = args.port
      callback()
    })

const register = cli.command(`register <username> <password>`)
register
  .description(`Registers a username and password on a database on the current connection.`)
  .alias('reg', 'r')
  .action(function (args, callback) {
    let username = args.username + ''
    if (username.indexOf('*') > -1 || username.indexOf('\\') > -1 || username.indexOf('/') > -1) {
      this.log(chalk.bold.red(`invalid username entered.`))
      callback()
    } else {
      server = net.createConnection(port, address, () => {
        let user
        hash(args.password + '')
          .then((hashedPassword) => user = new User(-1, username, hashedPassword))
          .then(() => server.write(`${JSON.stringify({clientMessage: {message: 'register', data: `${JSON.stringify(user)}`}})}\n`))
        // I promise to make this a Promise later
        processServer(server, args, callback)
      })
    }
  })

const login = cli .command(`login <username> <password>`)
login
  .description(`Log in as a user to the current server connection using username and password.`)
  .alias('log', 'l')
  .action(function (args, callback) {
    server = net.createConnection(port, address, () => {
      let user = new User(-1, args.username, 'Daimen is the cooliest')
      // Username is all we care about server side, client side checks with password.
      server.write(`${JSON.stringify({clientMessage: {message: 'login', data: `${JSON.stringify(user)}`}})}\n`)

      processServer(server, args, callback)
    })
  })

const files = cli.command(`files`)
files
  .description(`Display a list of your files on server and their ID if logged in.`)
  .alias('file', 'f')
  .action(function (args, callback) {
    server = net.createConnection(port, address, () => {
      server.write(`${JSON.stringify({clientMessage: {message: `files${sessionID}`, data: `Daimen is the best`}})}\n`)

      processServer(server, args, callback)
    })
  })

const upload = cli.command(`upload <local_filepath> [server_filepath]`)
upload
  .description(`Upload a given file to database if logged in.`)
  .alias('up', 'u')
  .action(function (args, callback) {
    let filepath = args.local_filepath
    if ('server_filepath' in args) {
      filepath = args.server_filepath
      if (filepath.indexOf('/') < 0) {
        if (filepath.indexOf('\\') < 0) {
          filepath = '/' + filepath
        } else {
          filepath = '\\' + filepath
        }
      }
    }
    filePromise(args.local_filepath)
      .then((data) => new FileD(-1, filepath, base64.fromByteArray(data)))
      .then((fileD) =>
        server = net.createConnection(port, address, () => {
          server.write(`${JSON.stringify({clientMessage: {message: `upload${sessionID}`, data: `${JSON.stringify(fileD)}`}})}\n`)

          processServer(server, args, callback)
        })
      )
  })
const download = cli.command(`download <database_file_id> [local_filepath]`)
download
  .description(`Download file from databse to database filepath, or local filepath if logged in`)
  .alias('down', 'd')
  .action(function (args, callback) {
    let num = args.database_file_id
    if (num < 0) {
      this.log(chalk.bold.red(`invalid file id entered.`))
    } else {
      server = net.createConnection(port, address, () => {
        server.write(`${JSON.stringify({clientMessage: {message: `download${sessionID}`, data: `"${num}"`}})}\n`)

        processServer(server, args, callback)
      })
    }
  })
const logout = cli.command(`logout`)
logout
  .description(`logs out of the current session`)
  .alias('lo')
  .action(function (args, callback) {
    if (sessionID === '') {
      this.log(`You are not currently logged in.`)
    } else {
      this.log(chalk.green(`You are now logged out.`))
      sessionID = ''
      cli.delimiter(`${DEFAULT_DELIMITER}`)
    }
    callback()
  })

export default cli

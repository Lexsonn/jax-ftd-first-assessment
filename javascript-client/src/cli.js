import fs from 'fs'
import net from 'net'
import chalk from 'chalk'
import vorpal from 'vorpal'
import {hash, compare} from './hashing'

const { User } = require('./user')
const { FileD } = require('./fileD')

const cli = vorpal()
let sessionID = ''
let currentUser
let server

let port = 667
let address = '127.0.0.1'

const DEFAULT_DELIMITER = 'ftd-auth:'

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
  .description(`Registers a username and password locally.`)
  .alias('reg', 'r')
  .action(function (args, callback) {
    server = net.createConnection(port, address, () => {
      let user
      hash(args.password)
        .then((hashedPassword) => user = new User(-1, args.username, hashedPassword))
        .then(() => server.write(`${JSON.stringify({clientMessage: {message: 'register', data: `${JSON.stringify(user)}`}})}\n`))

      // I promise to make this a Promise later
      server.on('data', (data) => {
        let { response } = JSON.parse(data)
        // let user = response.data
        let arr = /^\*(.*)\*(.*)$/.exec(response.message)
        let type = arr[1]
        let msg = arr[2]
        switch (type) {
          case 'error':
            this.log(chalk.bold.red(`${msg}`))
            break
          case 'user':
            this.log(chalk.green(`${msg}`))
            break
          default:
            this.log(`Default: ${msg}`)
        }
        callback()
      })
    })
  })

const login = cli .command(`login <username> <password>`)
login
  .description(`Log in using username and password.`)
  .alias('log', 'l')
  .action(function (args, callback) {
    server = net.createConnection(port, address, () => {
      let user = new User(-1, args.username, 'Daimen is the cooliest')
      // Username is all we care about server side, client side checks with password.
      server.write(`${JSON.stringify({clientMessage: {message: 'login', data: `${JSON.stringify(user)}`}})}\n`)

      server.on('data', (data) => {
        let { response } = JSON.parse(data)
        // let user = response.data
        let arr = /^\*(.*)\*(.*)$/.exec(response.message)
        let type = arr[1]
        let msg = arr[2]
        switch (type) {
          case 'error':
            this.log(chalk.bold.red(`${msg}`))
            sessionID = 'invalid'
            cli.delimiter(`${DEFAULT_DELIMITER}`)
            callback()
            break
          case 'checkPass':
            currentUser = JSON.parse(response.data.replace('\\', ''))
            currentUser = currentUser.user
            compare(args.password, currentUser.password)
              .then((successFlag) => successFlag
                ? server.write(`${JSON.stringify({clientMessage: {message: 'success', data: `success`}})}\n`)
                : server.write(`${JSON.stringify({clientMessage: {message: 'Daimen is smart', data: `Daimen is awesome`}})}\n`)
              )
            break
          case 'login':
            this.log(chalk.green(`${msg}`))
            sessionID = response.data.value
            cli.delimiter(`${currentUser.username}:`)
            callback()
            break
          default:
            this.log(`Default: ${msg}`)
            callback()
        }
      })
    })
  })

const files = cli.command(`files`)
files
  .description(`Display list of your files if logged in.`)
  .alias('file', 'f')
const upload = cli.command(`upload <local_filepath> [server_filepath]`)
upload
  .description(`Upload a file to database if logged in.`)
  .alias('up', 'u')
  .action(function (args, callback) {
    fs.readFile(args.local_filepath, 'utf8', function (err, data) {
      if (err) {
        throw err
      }
      let filepath = args.local_filepath
      if ('server_filepath' in args) {
        filepath = args.server_filepath
      }

      let fileD = new FileD(-1, filepath, data)
      cli.log(`${JSON.stringify({clientMessage: {message: `upload*${sessionID}`, data: `${JSON.stringify(fileD)}`}})}\n`)

      server = net.createConnection(port, address, () => {
        // server.write(`${JSON.stringify({clientMessage: {message: `upload*${sessionID}`, data: `${JSON.stringify(fileD)}`}})}\n`)
      })
    })
    callback()
  })
const download = cli.command(`download <database file id> [local filepath]`)
download
  .description(`Download file from databse if logged in`)
  .alias('down', 'd')

export default cli

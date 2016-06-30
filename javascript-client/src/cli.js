import net from 'net'
import chalk from 'chalk'
import vorpal from 'vorpal'
import {hash, compare} from './hashing'

const { User } = require('./user')

const cli = vorpal()
let sessionID = ''
let currentUser
let server

cli
  .delimiter('ftd-auth:')

const register = cli.command(`register <username> <password>`)
register
  .description(`Registers a username and password locally.`)
  .alias('reg', 'r')
  .action(function (args, callback) {
    server = net.createConnection(667, '127.0.0.1', () => {
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
    server = net.createConnection(667, '127.0.0.1', () => {
      let user = new User(-1, args.username, 'Daimen is the cooliest')
      // Username is all we care about server side, client side checks with password.
      server.write(`${JSON.stringify({clientMessage: {message: 'login', data: `${JSON.stringify(user)}`}})}\n`)

      server.on('data', (data) => {
        this.log(data.toString())
        let { response } = JSON.parse(data)
        // let user = response.data
        let arr = /^\*(.*)\*(.*)$/.exec(response.message)
        let type = arr[1]
        let msg = arr[2]
        switch (type) {
          case 'error':
            this.log(chalk.bold.red(`${msg}`))
            sessionID = 'invalid'
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
            this.log(`${sessionID}`)
            callback()
            break
          default:
            this.log(`Default: ${msg}`)
            callback()
        }
      })
    })
  })

const ls = cli.command(`ls`)
ls
  .description(`Display a list of users`)
  .alias('users')
  .action(function (args, callback) {
    this.log(sessionID)
    this.log(currentUser)
    callback()
  })

export default cli

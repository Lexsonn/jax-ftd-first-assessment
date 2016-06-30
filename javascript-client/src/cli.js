import net from 'net'
import chalk from 'chalk'
import vorpal from 'vorpal'
import {hash, compare} from './hashing'

const ClientMessage = require('./clientMessage')

const cli = vorpal()
const users = {}
let server

cli
  .delimiter('ftd-auth:')

const register = cli.command(`register <username> <password>`)
register
  .description(`Registers a username and password locally.`)
  .alias('reg', 'r')
  .action(function (args, callback) {
    server = net.createConnection(args, () => {
      let address = server.address()
      this.log(`connected to server ${address.address}:${address.port}`)

      let user = new User(0, args.username, 'undefined')
      let clientMessage = new ClientMessage('register', user)

      server.write(JSON.stringify())

      callback()
    })
    return (
      Promise.resolve(users[args.username] !== undefined)
      .then((alreadyRegistered) => alreadyRegistered
        ? this.log(chalk.bold.red(`Username ${args.username} is already registered.`))
        : hash(args.password)
          .then((hashedPassword) => users[args.username] = hashedPassword)
          .then(() => this.log(chalk.green(`Username ${args.username} sucessfully registered.`)))
      )
    )
  })

const login = cli .command(`login <username> <password>`)
login
  .description(`Log in using username and password.`)
  .alias('log', 'l')
  .action(function (args, callback) {
    return (
      Promise.resolve(users[args.username])
      .then((hashedPassword) => hashedPassword === undefined
        ? this.log(chalk.bold.red(`Login credentials are incorrect.`))
        : compare(args.password, hashedPassword)
          .then((successFlag) => successFlag
            ? this.log(chalk.green(`Successfully logged in!`))
            : this.log(chalk.bold.red(`Login credentials are incorrect.`))
          )
      )
    )
  })

const ls = cli.command(`ls`)
ls
  .description(`Display a list of users`)
  .alias('users')
  .action(function (args, callback) {
    this.log(users)
    callback()
  })

export default cli

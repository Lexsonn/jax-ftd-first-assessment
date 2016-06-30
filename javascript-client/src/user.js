class User {

  constructor (userId, username, hashedPass) {
    this.userId = userId
    this.username = username
    this.password = hashedPass
  }
}

module.exports = {
  User
}

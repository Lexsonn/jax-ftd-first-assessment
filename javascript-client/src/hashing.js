import bcrypt from 'bcryptjs'

/**
 *  @param password [string] - the password to be hashed
 *  @return [Promise] - a promise of the hashed password
 */
export function hash (password) {
  return new Promise(function executor (resolve, reject) {
    bcrypt.genSalt(function (err, salt) {
      if (err) {
        reject(err)
      } else {
        bcrypt.hash(password, salt, function (err, hashedPassword) {
          if (err) {
            reject(err)
          } else {
            resolve(hashedPassword)
          }
        })
      }
    })
  })
}

/**
 *  @param password [string] - plain text password to compare
 *  @param hashedPassword [string] - hashed password to compare
 *  @return [Promise] - promise containing comparison result (true if sucessfull, false otherwise)
 */
export function compare (password, hashedPassword) {
  return new Promise(function executor (resolve, reject) {
    bcrypt.compare(password, hashedPassword, function (err, successFlag) {
      if (err) {
        reject(err)
      } else {
        resolve(successFlag)
      }
    })
  })
}

export default {
  hash,
  compare
}

import fs from 'fs'

class FileD {

  constructor (fileId, filepath, file) {
    this.fileId = fileId
    this.filepath = filepath
    this.file = file
  }
}

function filePromise (path) {
  return (
    new Promise((resolve, reject) => {
      fs.readFile(path, (err, data) => {
        if (err) {
          reject(err)
        } else {
          resolve(data)
        }
      })
    })
  )
}

function fileWritePromise (path, data) {
  return new Promise(function executor (resolve, reject) {
    fs.writeFile(path, data, function (err, successFlag) {
      if (err) {
        reject(err)
      } else {
        resolve(true)
      }
    })
  })
}

module.exports = {
  FileD,
  filePromise,
  fileWritePromise
}

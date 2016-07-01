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

module.exports = {
  FileD,
  filePromise
}

var express = require("express");
const route = express.Router();
var multer, storage, path, crypto;
multer = require('multer')
path = require('path');
crypto = require('crypto');

var form = "<!DOCTYPE HTML><html><body>" +
"<form method='post' action='/upload' enctype='multipart/form-data'>" +
"<input type='file' name='upload'/>" +
"<input type='submit' /></form>" +
"</body></html>";

route.get('/', function (req, res){
  res.writeHead(200, {'Content-Type': 'text/html' });
  res.end(form);
});

// Include the node file module
var fs = require('fs');

storage = multer.diskStorage({
          destination: './API/uploads/',
          filename: function(req, file, cb) {
            return crypto.pseudoRandomBytes(16, function(err, raw) {
              if (err) {
                return cb(err);
              }
              return cb(null, "" + (raw.toString('hex')) + (path.extname(file.originalname)));
            });
          }
        });


// Post files
route.post(
  "/upload",
          multer({ storage: storage }).single('upload'), function(req, res) {
              res.redirect("/uploads/" + req.file.filename);
              return res.status(200).end();
        });

route.get('/uploads/:upload', function (req, res){
    file = req.params.upload;
    console.log(req.params.upload);
    var img = fs.readFileSync(__dirname + "/uploads/" + file);
    res.writeHead(200, {'Content-Type': 'image/png' });
    res.end(img, 'binary');
  });

module.exports = route;
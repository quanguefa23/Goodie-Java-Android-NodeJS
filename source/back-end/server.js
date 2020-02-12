const express = require('express');
const bodyParser = require('body-parser');
const connectDB = require('./DB/Connection');
const app = express();
const publicDir = (__dirname + '/public/');


connectDB();
app.use(express.static(publicDir));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.use('', require('./API/User'));
app.use('', require('./API/Product'));
app.use('', require('./API/Notification'));
app.use('', require('./API/Upload'));

app.listen(process.env.PORT || 3001, () => {
    console.log('Server running on port 3000');
})
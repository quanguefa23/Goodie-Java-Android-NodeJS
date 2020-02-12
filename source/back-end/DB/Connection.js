const mongoose = require('mongoose');
const URI = "mongodb+srv://Goodie:datcuto102@goodiedb-feb2w.mongodb.net/GoodieDB?retryWrites=true&w=majority";
const connectDB = async () => {
    await mongoose.connect(URI, {
        useUnifiedTopology: true,
        useNewUrlParser: true,
        useCreateIndex: true
    });
    console.log("db connected..!");
}

module.exports = connectDB;
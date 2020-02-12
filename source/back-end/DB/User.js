const mongoose = require('mongoose');

const schema = new mongoose.Schema({
    fullname: { type: String }, 
    phone: { type: String,  unique: true},
    mail: { type: String, unique: true}, 
    sex: { type: String }, 
    pass: { type: String }, 
    dob: { type: String }
    },
    {
        collection: 'users',
    });

module.exports = User = mongoose.model('User', schema);
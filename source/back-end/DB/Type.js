const mongoose = require('mongoose');

const schema = new mongoose.Schema({
    id: { type: String }, 
    type: { type: String }
    },
    {
        collection: 'type',
    });

module.exports = Type = mongoose.model('Type', schema);
const mongoose = require('mongoose');

const schema = new mongoose.Schema({
    title: { type: String }, 
    price: { type: String},
    img: { type: String}, 
    type: { type: String }, 
    location: { type: String }, 
    producer: { type: String },
    state: { type: String}, 
    color: { type: String }, 
    more: { type: String }, 
    owner: { type: String },
    address: { type: String}, 
    phone: { type: String }, 
    date: { type: String }, 
    time: { type: String },
    sold: { type: Number },
    timeValue: {type: Number}
    },
    {
        collection: 'product',
    });

module.exports = Product = mongoose.model('Product', schema);
const mongoose = require('mongoose');
const schema = new mongoose.Schema({
    userId: {type: String},
    type: { type: Number }, 
    from: { type: String },
    mark: { type: Boolean}, 
    },
    {
        collection: 'notification',
    });

module.exports = Notification = mongoose.model('Notification', schema);
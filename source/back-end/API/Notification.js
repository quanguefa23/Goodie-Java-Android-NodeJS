const express = require('express');
const mongoose = require('mongoose');
const route = express.Router();

const Notification = require('../DB/Notification');
const User = require('../DB/User');

route.get('/notice', async(req, res) => {
    const {user} = req.query;

    
    try{
        const data = await User.findOne({$or: [ { mail: user}, { phone: user} ]});
        if (!data) {
            res.json(false);
        }
        const pipeline = [
            {
                $match: {
                    userId: data._id
                },
            },
            {
                $project: {
                id: '$_id',
                type: true,
                from: true,
                mark: true,
                _id: false,
                },
            },
        ]

        const notify = await Notification.aggregate(pipeline).allowDiskUse(true);
        res.json(notify[0]?notify:false);
    }
    catch(e) {
        res.json(false);
    }

})

route.get('/seen', async(req, res) => {
    const {id} = req.query;

    
    try{
        await Notification.findOneAndUpdate(
            { _id : id },
            { mark : false });
        res.json(true);
    }
    catch(e) {
        res.json(false);
    }
})

route.get('/refresh', async(req, res) => {

    try{
        await Notification.updateMany({},
            { $set: {mark : true }});
        res.json(true);
    }
    catch(e) {
        res.json(false);
    }
})

module.exports = route;
const express = require('express');
const mongoose = require('mongoose');

const User = require('../DB/User');
const route = express.Router();
route.get('/adduser', async(req, res) => {
    const newUser = req.query;
    try{
        const data = await User.findOne(
            {$or: [ { mail: newUser.mail }, { phone: newUser.phone } ]});
        if (data) {
            res.json(false);
        }
        else {
            await User.insertMany(newUser);
            res.json(true);
        }
    }
    catch(e) {
        res.json(false);
    }

})

route.get('/login', async(req, res) => {
    const {user, pass} = req.query;
    try{
        const data = await User.findOne({$or: [ { mail: user}, { phone: user} ]});
        if (data && data.pass === pass) {
            res.json(true);
        }
        else {
            res.json(false);
        }
    }
    catch(e) {
        res.json(false);
    }

})

route.get('/getuserinfo', async(req, res) => {
    const {user} = req.query;
    try{
        const data = await User.findOne({$or: [ { mail: user}, { phone: user} ]});
        res.json(data ? data : false);
    }
    catch(e) {
        res.json(false);
    }

})


module.exports = route;
const PRODUCT_LIMIT = 3; 

const express = require('express');
const mongoose = require('mongoose');
const moment = require('moment');
const route = express.Router();

const Product = require('../DB/Product');
const Type = require('../DB/Type');

route.get('/product', async(req, res) => {
    const {type} = req.query;
    const pipeline = [
        {
            $match: {
                ...(type != 0 && { type: type }),
            },
        },
        {
            $sort: {timeValue: -1}
        },
        {
            $limit: PRODUCT_LIMIT,
        },
        {
            $project: {
              id: '$_id',
              title: true,
              price: true,
              img: true,
              _id: false,
            },
        },
    ]

    try{
        const data = await Product.aggregate(pipeline).allowDiskUse(true);
        res.json(data[0]?data:false);
    }
    catch(e) {
        res.json(false);
    }

})

route.post('/product', async(req, res) => {
    let product = req.query;
    product.date = moment().format("MM/DD/YYYY");
    product.time = moment().format("hh:mm A");
    product.timeValue = new Date().getTime();
    product.sold = 0;
    product.price = String(parseInt(product.price)).replace(/(.)(?=(\d{3})+$)/g,'$1.') + " VNĐ";
    try{
        await Product.insertMany(product);
        res.json(true);
    }
    catch(e) {
        res.json(false);
    }

})

route.delete('/product', async(req, res) => {
    const {id} = req.query;
    try{
        await Product.deleteOne({_id: id});
        res.json(true);
    }
    catch(e) {
        res.json(false);
    }

})

route.get('/detail', async(req, res) => {
    const {id} = req.query;
    
    try{
        let data = await Product.findOne({_id: id});
        if (!data) {
            res.json(false);
        }
        const {type = ""} = await Type.findOne({...(data.type && {id: data.type})});
        data.type = type; 
        res.json(data);
    }
    catch(e) {
        res.json(false);
    }

})


module.exports = route;
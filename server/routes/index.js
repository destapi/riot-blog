const express = require('express');
const router = express.Router();

const todoRoutes = require('./todos');
const eventRoutes = require('./events');

router.use('/todos', todoRoutes);
router.use('/events', eventRoutes);

module.exports = router;

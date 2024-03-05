const express = require('express');
const router = express.Router();

const todoRoutes = require('./todos2');
const eventRoutes = require('./events2');

router.use('/todos', todoRoutes);
router.use('/events', eventRoutes);

module.exports = router;

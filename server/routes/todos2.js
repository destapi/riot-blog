const express = require('express');
const router = express.Router();
const {getAll, add, toggle} = require('../store/reddy');

router.get('/', async function(req, res, next) {
  res.json(await getAll("todos"));
});

router.post('/:title', async function(req, res, next) {
  const title = req.params['title'];
  await add("todos", title);
  res.sendStatus(200);
});

router.put('/:title', async function(req, res, next) {
  const title = req.params['title'];
  await toggle("todos", title);
  res.sendStatus(200);
});

module.exports = router;

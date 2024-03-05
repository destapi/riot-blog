const express = require('express');
const router = express.Router();
const store = require('../store');

router.get('/', function(req, res, next) {
  res.json(store.getState().todos);
});

router.post('/:title', function(req, res, next) {
  const title = req.params['title'];
  store.getState().add(title);
  res.sendStatus(200);
});

router.put('/:title', function(req, res, next) {
  const title = req.params['title'];
  store.getState().toggle(title);
  res.sendStatus(200);
});

module.exports = router;

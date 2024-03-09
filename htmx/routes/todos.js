var express = require('express');
var router = express.Router();

const todos = [
  {id: 1, title: "work out"},
  {id: 2, title: "read book"}
]

/* GET todos list. */
router.get('/', function(req, res, next) {
  res.send(`
  <h2 class="text-2xl font-bold my-4">Tasks List</h2>
  <ul>
  ${todos.map(todo => `
    <li id=${todo.id}>${todo.title}</li>
  `).join('')}
  </ul>
  `);
});

module.exports = router;

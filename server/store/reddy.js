const {createClient} = require('redis');

exports.getAll = async function (listName) {
    try {
        const client = await createClient()
            .on('error', err => console.log('Redis Client Error', err))
            .connect();

        const todos = await client.lRange(listName, 0, -1);
        return todos.map(JSON.parse)
    } catch (e) {
        console.log(e)
    }
}

exports.add = async function (listName, title) {
    try {
        const client = await createClient()
            .on('error', err => console.log('Redis Client Error', err))
            .connect();

        const todo = ({title, done: false})

        const todoStr = JSON.stringify(todo)
        await client.rPush(listName, todoStr);
        await client.publish("add", todoStr);   //add statement to publish event
        await client.disconnect();
    } catch (e) {
        console.log(e)
    }
}

exports.toggle = async function (listName, title) {
    try {
        const client = await createClient()
            .on('error', err => console.log('Redis Client Error', err))
            .connect();

        const todos = await client.lRange(listName, 0, -1);
        let payload = []
        for (let i = 0; i < todos.length; i++) {
            let todo = JSON.parse(todos[i]);
            if (todo.title === title) {
                todo.done = !todo.done
                await client.lSet(listName, i, JSON.stringify(todo));
            }
            payload.push(todo);
        }
        await client.publish("toggle", JSON.stringify(payload));      //add statement to publish event
        await client.disconnect();
    } catch (e) {
        console.log(e)
    }
}

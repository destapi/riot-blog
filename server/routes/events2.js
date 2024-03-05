const express = require('express');
const router = express.Router();
const {createClient} = require('redis');

const writers = {}

router.get('/:user', async function (req, res) {
    const user = req.params["user"];
    if (writers[user]) {
        console.log(`closing previous connection before saving a new one for ${user}`)
        writers[user].end('OK')
    }
    writers[user] = res;

    //keep connection open
    res.set({
        'Cache-Control': 'no-cache',
        'Content-Type': 'text/event-stream',
        'Connection': 'keep-alive'
    });
    res.flushHeaders();

    // Tell the client to retry every 10 seconds if connectivity is lost
    res.write('retry: 10000\n\n');

    //Now handle business
    let counter = 0;

    const listener = ((message, channel) => {
        console.log('channel\n\n', channel, 'message\n\n', message);
        if (channel === "add") {
            res.write('event: add\n');
            res.write(`data: ${message}\n`);
            res.write(`id: ${counter}\n\n`);
        } else {
            res.write('event: toggle\n');
            res.write(`data: ${message}\n`);
            res.write(`id: ${counter}\n\n`);
        }
        counter++;
    })

    try {
        const client = await createClient()
            .on('error', err => console.log('Redis Client Error', err))
            .connect();

        await client.subscribe(['add', 'toggle'], listener);
    } catch (e) {
        console.log(e)
    }

    // Close the connection when the client disconnects
    req.on('close', () => res.end('OK'))
});

module.exports = router;

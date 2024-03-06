export function startEvents({ remoteUrl, addTodo, toggleTodos }) {
    const sub = new EventSource(`${remoteUrl}/events/jimmy`);

    // Default events
    sub.addEventListener('open', () => {
        console.log('Connection opened')
    });

    sub.addEventListener('error', () => {
        console.error("Subscription err'd")
    });

    sub.addEventListener('add', (event) => {
        const data = JSON.parse(event.data);
        addTodo(data);
    });

    sub.addEventListener('toggle', (event) => {
        const data = JSON.parse(event.data);
        toggleTodos(data);
    });
}
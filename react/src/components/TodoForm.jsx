import { useState } from 'react';

export default function TodoForm(props) {

    const [text, setText] = useState('')

    function edit(e) {
        // update only the text state
        setText(e.target.value)
    }

    function add(e) {
        e.preventDefault()

        if (text) {
            props.add(text)
            setText('');
        }
    }

    return (
        <form onSubmit={add}>
            <label>
                <input onChange={edit} value={text} />
            </label>
            <button disabled={!text}>
                Add #{props.count}
            </button>
        </form>
    )
}
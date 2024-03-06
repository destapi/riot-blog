
export default function TodoItems(props){

    return (
        <ul>
            {props.items.map((item, i) => (
            <li key={i}>
            <label className={ item.done ? 'completed' : null }>
                <input
                type="checkbox"
                readOnly
                checked={ item.done }
                onClick={ () => props.toggle(item) } />
                { item.title }
            </label>
            </li>))}
        </ul>
    )
}
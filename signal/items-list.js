import { createSignal } from "./reactive.js";

const[items, setItems] = createSignal([{ id: 1, title: "first" }, { id: 2, title: "second" }]);  

export class ItemsList extends HTMLElement {

    constructor() {
        super();
        this.container = null;
        this.items = null;
        this.key = null;
    }

    // component attributes
    static get observedAttributes() {
        return ['container', 'items', 'key'];
    }

    // attribute change
    attributeChangedCallback(property, oldValue, newValue) {
        if (oldValue === newValue) return;
        this[property] = newValue;
    }

    connectedCallback() {
        if (!this.container || !this.items || !this.key) {
            throw Error("the attributes [container, items, key] are not optional");
        }
        const el = document.createElement(this.container);
        const firstChild = this.firstElementChild
        this.removeChild(firstChild);
        for (let item of items()) {
            el.appendChild(firstChild.cloneNode(true))
        }
        this.appendChild(el);
    }

    removeAll() {
        while (this.hasChildNodes()) {
            this.removeChild(this.firstChild);
        }
    }
}

customElements.define('items-list', ItemsList);
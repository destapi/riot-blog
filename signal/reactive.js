export const $ = {};

let currentListener = undefined;

export function createSignal(initialValue) {
	let value = initialValue;

	const subscribers = new Set();

	const read = () => {
		if (currentListener !== undefined) {
			subscribers.add(currentListener);
		}
		return value;
	};
	const write = (newValue) => {
		value = newValue;
		subscribers.forEach((fn) => fn());
	};

	return [read, write];
}

export function createEffect(callback) {
	currentListener = callback;
	callback();
	currentListener = undefined;
}
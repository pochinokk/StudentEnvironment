class PixelAvatar extends HTMLElement {
    constructor() {
        super();
        this.attachShadow({ mode: 'open' });
    }

    connectedCallback() {
        const size = parseInt(this.getAttribute('size') || '5');
        const pixelSize = parseInt(this.getAttribute('pixel-size') || '5');
        const matrix = this.generateSymmetricMatrix(size);

        const neonColors = [
            '#a0f8b0', // мятный
            '#ffa3b1', // пастельно-розовый
            '#aeceff', // светло-голубой
            '#dab6fc', // светлая сирень
            '#e3c100'  // тёмно-жёлтый (золотистый)
        ];

        const color = neonColors[Math.floor(Math.random() * neonColors.length)];

        const container = document.createElement('div');
        container.className = 'pixel-grid';
        container.style.display = 'grid';
        container.style.border = '1px solid black';
        container.style.gridTemplateColumns = `repeat(${size}, ${pixelSize}px)`;

        matrix.forEach(row => {
            row.forEach(value => {
                const div = document.createElement('div');
                div.style.width = `${pixelSize}px`;
                div.style.height = `${pixelSize}px`;
                div.style.backgroundColor = value === 1 ? color : 'white';
                container.appendChild(div);
            });
        });

        this.shadowRoot.appendChild(container);
    }

    generateSymmetricMatrix(size) {
        const matrix = [];
        for (let row = 0; row < size; row++) {
            const half = [];
            const halfSize = Math.ceil(size / 2);
            for (let i = 0; i < halfSize; i++) {
                half.push(Math.random() < 0.5 ? 0 : 1);
            }
            const fullRow = [...half, ...half.slice(0, size % 2 === 0 ? halfSize : halfSize - 1).reverse()];
            matrix.push(fullRow);
        }
        return matrix;
    }
}

customElements.define('pixel-avatar', PixelAvatar);

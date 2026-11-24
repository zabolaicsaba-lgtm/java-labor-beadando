const canvas = document.getElementById("megye-diagram");
const rows = document.querySelectorAll("#megyei-adatok tbody tr");

if (canvas && rows.length) {
    const labels = Array.from(rows, row => row.cells[0].textContent);
    const data = Array.from(rows, row => Number(row.cells[1].textContent));

    new Chart(canvas, {
        type: "bar",
        data: {
            labels,
            datasets: [{
                label: "Tornyok száma",
                data,
                backgroundColor: "#267c5c"
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { precision: 0 }
                }
            }
        }
    });
}

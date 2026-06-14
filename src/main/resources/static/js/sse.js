const eventSource = new EventSource("/api/sse/orders");

eventSource.addEventListener("order-update", function (event) {
    const [orderId, status] = event.data.split(": ");
    const row = document.querySelector('tr[data-id="' + orderId + '"]');
    if (!row) return;

    const badge = row.querySelector(".status");
    if (!badge) return;

    badge.textContent = status;
    badge.className = "status status-" + status;
});

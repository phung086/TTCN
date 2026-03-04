document.addEventListener("DOMContentLoaded", async () => {
  loadDashboardStats();
  loadReservations();
});

async function loadDashboardStats() {
  try {
    const response = await fetch("/api/v1/admin/dashboard");
    if (!response.ok) throw new Error("Failed to load dashboard data");

    const data = await response.json();

    if (document.getElementById("totalRevenue"))
      document.getElementById("totalRevenue").innerText = formatCurrency(
        data.monthlyRevenue,
      );

    if (document.getElementById("activeReservations"))
      document.getElementById("activeReservations").innerText =
        data.activeReservations;

    if (document.getElementById("totalGuests"))
      document.getElementById("totalGuests").innerText = data.totalGuests;

    if (document.getElementById("totalRooms"))
      document.getElementById("totalRooms").innerText = data.totalRooms;
  } catch (error) {
    console.error("Error loading dashboard stats:", error);
  }
}

async function loadReservations() {
  const tableBody = document.getElementById("reservationTableBody");
  // tableBody.innerHTML = '<tr><td colspan="6" class="text-center">Đang tải...</td></tr>';
  // Commented out to prevent flicker on manual refresh if desired, but good for feedback

  try {
    // Fetch all reservations.
    // Note: Real apps should use pagination. We assume the API returns a list.
    const response = await fetch("/api/v1/reservations"); // Does this endpoint exist and return list?
    // Need to check ReservationController. It likely returns a single reservation by ID or page.
    // If it returns page, we need content. If error (404/405), we handle it.

    if (!response.ok)
      throw new Error(
        "Không lấy được danh sách (API có thể chưa hỗ trợ lấy tất cả)",
      );

    const reservations = await response.json();

    // Handle if response is Page<Reservation> or List<Reservation>
    const list = Array.isArray(reservations)
      ? reservations
      : reservations.content || [];

    if (!list || list.length === 0) {
      tableBody.innerHTML =
        '<tr><td colspan="6" class="text-center text-muted">Chưa có dữ liệu đặt phòng.</td></tr>';
      return;
    }

    // Sort by ID desc
    list.sort((a, b) => b.id - a.id);

    tableBody.innerHTML = list
      .map((r) => {
        let actions = `<div class="btn-group" role="group">`;

        if (r.status === "PENDING" || r.status === "CONFIRMED") {
          actions += `<button class="btn btn-sm btn-success" title="Check-in" onclick="updateStatus(${r.id}, 'check-in')"><i class="fas fa-check"></i></button>`;
          actions += `<button class="btn btn-sm btn-danger" title="Hủy" onclick="updateStatus(${r.id}, 'cancel')"><i class="fas fa-times"></i></button>`;
        } else if (r.status === "CHECKED_IN") {
          actions += `<button class="btn btn-sm btn-warning" title="Check-out" onclick="updateStatus(${r.id}, 'check-out')"><i class="fas fa-sign-out-alt"></i></button>`;
          actions += `<button class="btn btn-sm btn-info" title="Thêm Dịch Vụ" onclick="showAddServiceModal(${r.id})"><i class="fas fa-plus-circle"></i></button>`;
        } else if (r.status === "CHECKED_OUT") {
          actions += `<button class="btn btn-sm btn-info" title="Xem Hóa Đơn" onclick="viewInvoice(${r.id})"><i class="fas fa-file-invoice-dollar"></i></button>`;
        } else {
          actions += `<span class="badge bg-secondary">Đã đóng</span>`;
        }
        actions += `</div>`;

        const guestName = r.guestName || 
          (r.guest ? (r.guest.lastName + " " + r.guest.firstName) : ("Khách #" + (r.guestId || "?")));
        const totalAmount = r.totalAmount
          ? formatCurrency(r.totalAmount)
          : "$0";

        return `
                <tr>
                    <td><strong>#${r.id}</strong></td>
                    <td>${guestName}</td>
                    <td>${r.checkInDate}</td>
                    <td>${r.checkOutDate}</td>
                    <td>${totalAmount}</td>
                    <td><span class="badge bg-${getStatusColor(r.status)}">${translateStatus(r.status)}</span></td>
                    <td>${actions}</td>
                </tr>
            `;
      })
      .join("");
  } catch (error) {
    console.error(error);
    tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Lỗi: ${error.message} hoặc API chưa hỗ trợ liệt kê.</td></tr>`;
  }
}

async function updateStatus(id, action) {
  let confirmMsg = "";
  if (action === "check-in") confirmMsg = "Xác nhận Check-in cho khách này?";
  if (action === "check-out") confirmMsg = "Xác nhận Check-out và tạo hóa đơn?";
  if (action === "cancel")
    confirmMsg = "Bạn có chắc chắn muốn HỦY đơn đặt phòng này?";

  if (!confirm(confirmMsg)) return;

  try {
    const url = `/api/v1/reservations/${id}/${action}`;
    const response = await fetch(url, {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) {
      // Try to parse error message
      let errorMsg = "Thao tác thất bại";
      try {
        const err = await response.json();
        errorMsg = err.message || errorMsg;
      } catch (e) {}
      throw new Error(errorMsg);
    }

    const data = await response.json();
    if (action === "check-out") {
      loadReservations();
      loadDashboardStats();
      // Show invoice modal
      await showInvoiceModal(id);
    } else {
      alert("Cập nhật trạng thái thành công!");
      loadReservations();
      loadDashboardStats();
    }
  } catch (error) {
    alert("Lỗi: " + error.message);
  }
}

// Invoice Modal
let invoiceModal;
let currentInvoiceId = null;

async function showInvoiceModal(reservationId) {
  const modalEl = document.getElementById("invoiceModal");
  if (!invoiceModal) {
    invoiceModal = new bootstrap.Modal(modalEl);
  }
  document.getElementById("invoiceModalBody").innerHTML =
    '<div class="text-center"><div class="spinner-border text-primary" role="status"></div> Đang tải hóa đơn...</div>';
  document.getElementById("invoiceModalFooter").innerHTML =
    '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>';
  invoiceModal.show();

  try {
    const resp = await fetch(`/api/v1/reservations/${reservationId}/invoice`);
    if (!resp.ok) throw new Error("Không tải được hóa đơn");
    const inv = await resp.json();
    currentInvoiceId = inv.id;

    const isPaid = inv.status === "PAID";
    document.getElementById("invoiceModalBody").innerHTML = `
      <table class="table table-sm">
        <tr><td>Tiền phòng</td><td class="text-end">${formatCurrency(inv.roomSubtotal)}</td></tr>
        <tr><td>Dịch vụ</td><td class="text-end">${formatCurrency(inv.serviceSubtotal)}</td></tr>
        <tr><td>Thuế (10%)</td><td class="text-end">${formatCurrency(inv.tax)}</td></tr>
        ${inv.discount > 0 ? `<tr><td>Giảm giá</td><td class="text-end text-danger">-${formatCurrency(inv.discount)}</td></tr>` : ""}
        <tr class="table-primary fw-bold"><td>TỔNG CỘNG</td><td class="text-end fs-5">${formatCurrency(inv.total)}</td></tr>
      </table>
      <div class="alert ${isPaid ? "alert-success" : "alert-warning"} mt-2 mb-0">
        Trạng thái: <strong>${isPaid ? "✅ Đã Thanh Toán" : "⏳ Chưa Thanh Toán"}</strong>
      </div>
      ${!isPaid ? `
      <hr/>
      <h6>Ghi Nhận Thanh Toán</h6>
      <div class="row g-2">
        <div class="col-6">
          <input type="number" id="payAmount" class="form-control" value="${inv.total}" placeholder="Số tiền" step="0.01" min="0.01"/>
        </div>
        <div class="col-6">
          <select id="payMethod" class="form-select">
            <option value="CASH">Tiền Mặt</option>
            <option value="CARD">Thẻ</option>
            <option value="BANK_TRANSFER">Chuyển Khoản</option>
            <option value="EWALLET">Ví Điện Tử</option>
          </select>
        </div>
      </div>
      ` : ""}
    `;

    if (!isPaid) {
      document.getElementById("invoiceModalFooter").innerHTML = `
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
        <button type="button" class="btn btn-success" onclick="submitPayment(${inv.id}, ${reservationId})">
          <i class="fas fa-credit-card"></i> Xác Nhận Thanh Toán
        </button>
      `;
    } else {
      document.getElementById("invoiceModalFooter").innerHTML =
        '<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>';
    }
  } catch (err) {
    document.getElementById("invoiceModalBody").innerHTML =
      `<div class="alert alert-danger">${err.message}</div>`;
  }
}

async function submitPayment(invoiceId, reservationId) {
  const amount = document.getElementById("payAmount").value;
  const method = document.getElementById("payMethod").value;

  if (!amount || parseFloat(amount) <= 0) {
    alert("Vui lòng nhập số tiền hợp lệ!");
    return;
  }

  try {
    const response = await fetch(`/api/v1/invoices/${invoiceId}/payments`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ amount: parseFloat(amount), method: method }),
    });

    if (!response.ok) {
      const err = await response.json();
      throw new Error(err.message || "Thanh toán thất bại");
    }

    // Refresh the invoice view
    await showInvoiceModal(reservationId);
    loadReservations();
  } catch (err) {
    alert("Lỗi: " + err.message);
  }
}

async function viewInvoice(reservationId) {
  await showInvoiceModal(reservationId);
}


// Service Modal Logic
let serviceModal;
let servicesCache = [];

async function loadServicesForModal() {
  if (servicesCache.length > 0) return;
  try {
    const resp = await fetch("/api/v1/services");
    if (resp.ok) {
      servicesCache = await resp.json();
      const select = document.getElementById("serviceSelect");
      select.innerHTML = servicesCache
        .map(
          (s) =>
            `<option value="${s.id}">${s.name} ($${s.unitPrice.toFixed(2)})</option>`,
        )
        .join("");
    }
  } catch (e) {
    console.error("Failed to load services:", e);
  }
}

function showAddServiceModal(resId) {
  const modalEl = document.getElementById("addServiceModal");
  if (!modalEl) {
    console.error("Modal not found!");
    return;
  }
  document.getElementById("serviceResId").value = resId;
  loadServicesForModal();
  if (!serviceModal) {
    serviceModal = new bootstrap.Modal(modalEl);
  }
  serviceModal.show();
}

async function submitAddService() {
  const reservationId = document.getElementById("serviceResId").value;
  const serviceSelect = document.getElementById("serviceSelect");
  const serviceId = serviceSelect.value;
  const quantity = document.getElementById("serviceQuantity").value;

  if (!reservationId) return;

  try {
    const response = await fetch(
      `/api/v1/reservations/${reservationId}/services`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          serviceId: parseInt(serviceId),
          quantity: parseInt(quantity),
        }),
      },
    );

    if (!response.ok) {
      let errorMsg = "Không thể thêm dịch vụ";
      try {
        const err = await response.json();
        errorMsg = err.message || errorMsg;
      } catch (e) {}
      throw new Error(errorMsg);
    }

    alert("Thêm dịch vụ thành công!");
    if (serviceModal) serviceModal.hide();
    loadReservations(); // List will update with new total
  } catch (e) {
    alert("Lỗi: " + e.message);
  }
}

function formatCurrency(amount) {
  if (amount === undefined || amount === null) return "$0.00";
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount);
}

function getStatusColor(status) {
  switch (status) {
    case "PENDING":
      return "secondary";
    case "CONFIRMED":
      return "primary";
    case "CHECKED_IN":
      return "success";
    case "CHECKED_OUT":
      return "info";
    case "CANCELED":
      return "danger";
    case "COMPLETED":
      return "dark";
    default:
      return "light";
  }
}

function translateStatus(status) {
  const map = {
    PENDING: "Chờ XN",
    CONFIRMED: "Đã Xác Nhận",
    CHECKED_IN: "Đang Lưu Trú",
    CHECKED_OUT: "Đã Trả Phòng",
    CANCELED: "Đã Hủy",
    COMPLETED: "Hoàn Tất",
  };
  return map[status] || status;
}

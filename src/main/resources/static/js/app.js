document.addEventListener("DOMContentLoaded", () => {
  // Set min date to today
  const today = new Date().toISOString().split("T")[0];
  document.getElementById("checkIn").setAttribute("min", today);
  document.getElementById("checkOut").setAttribute("min", today);

  const searchForm = document.getElementById("checkAvailabilityForm");
  const roomList = document.getElementById("roomList");

  if (searchForm) {
    searchForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const checkIn = document.getElementById("checkIn").value;
      const checkOut = document.getElementById("checkOut").value;
      const guests = document.getElementById("guests").value;

      if (!checkIn || !checkOut) {
        alert("Vui lòng chọn ngày nhận phòng và trả phòng!");
        return;
      }

      if (checkIn >= checkOut) {
        alert("Ngày trả phòng phải sau ngày nhận phòng!");
        return;
      }

      roomList.innerHTML =
        '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"></div><p class="mt-2">Đang tìm phòng trống...</p></div>';

      try {
        // Call API
        const url = `/api/v1/rooms/available?checkIn=${checkIn}&checkOut=${checkOut}&guests=${guests}`;
        const response = await fetch(url);

        if (!response.ok) throw new Error("Không thể tải danh sách phòng");

        const rooms = await response.json();
        renderRooms(rooms);
      } catch (error) {
        console.error("Error:", error);
        roomList.innerHTML = `<div class="col-12 text-center text-danger">Có lỗi xảy ra khi tải dữ liệu. Vui lòng thử lại sau.</div>`;
      }
    });
  }

  function renderRooms(rooms) {
    if (!rooms || rooms.length === 0) {
      roomList.innerHTML =
        '<div class="col-12 text-center p-5"><i class="fas fa-search fa-3x text-muted mb-3"></i><p>Rất tiếc, không tìm thấy phòng trống nào phù hợp cho ngày đã chọn.</p></div>';
      return;
    }

    roomList.innerHTML = rooms
      .map(
        (room) => {
          const statusBadge = room.currentStatus && room.currentStatus !== "AVAILABLE"
            ? `<span class="badge bg-warning text-dark mb-2"><i class="fas fa-clock"></i> Đang có khách - Có thể đặt trước</span><br>`
            : "";
          return `
            <div class="col-md-4 mb-4">
                <div class="card h-100 shadow-sm room-card border-0">
                    <img src="https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80" class="card-img-top" alt="Room Image" style="height: 200px; object-fit: cover;">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title fw-bold">${room.roomType || "Phòng Tiêu Chuẩn"}</h5>
                        ${statusBadge}
                        <p class="card-text text-muted mb-1"><i class="fas fa-door-open"></i> Số phòng: ${room.roomNumber}</p>
                        <p class="card-text mb-3"><i class="fas fa-user-friends"></i> Sức chứa: ${room.capacity} Khách</p>
                        <div class="mt-auto d-flex justify-content-between align-items-center">
                            <span class="room-price text-danger fs-5 fw-bold">${formatCurrency(room.price)} <small class="text-muted fs-6">/ đêm</small></span>
                            <button class="btn btn-primary px-4" onclick="openBookingModal(${room.roomId})">Đặt Ngay</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        },
      )
      .join("");
  }
});

function formatCurrency(amount) {
  if (!amount) return "Liên hệ";
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount);
}

// Modal handling
let bookingModal;
function openBookingModal(roomId) {
  document.getElementById("selectedRoomId").value = roomId;
  bookingModal = new bootstrap.Modal(document.getElementById("bookingModal"));
  bookingModal.show();
}

async function submitBooking() {
  const roomId = document.getElementById("selectedRoomId").value;
  const firstName = document.getElementById("guestFirstName").value;
  const lastName = document.getElementById("guestLastName").value;
  const email = document.getElementById("guestEmail").value;
  const phone = document.getElementById("guestPhone").value;

  const checkIn = document.getElementById("checkIn").value;
  const checkOut = document.getElementById("checkOut").value;

  if (!firstName || !lastName || !email || !phone) {
    alert("Vui lòng điền đầy đủ thông tin khách hàng!");
    return;
  }

  // Show loading state
  const submitBtn = document.querySelector("#bookingModal .btn-primary");
  const originalBtnText = submitBtn.innerText;
  submitBtn.innerText = "Đang xử lý...";
  submitBtn.disabled = true;

  try {
    // 1. Create Guest
    const guestResponse = await fetch("/api/v1/guests", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        email: email.trim(),
        phone: phone.trim(),
      }),
    });

    if (!guestResponse.ok) {
      const err = await guestResponse.json();
      throw new Error(
        err.message ||
          "Lỗi khi tạo thông tin khách hàng (Có thể Email đã tồn tại)",
      );
    }

    const guest = await guestResponse.json();
    // If the API returned an existing guest (same email), their name may differ from form input

    // 2. Create Reservation
    const reservationResponse = await fetch("/api/v1/reservations", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        guestId: guest.id,
        roomIds: [parseInt(roomId)],
        checkInDate: checkIn,
        checkOutDate: checkOut,
      }),
    });

    if (!reservationResponse.ok) {
      const err = await reservationResponse.json();
      throw new Error(err.message || "Lỗi khi tạo đặt phòng");
    }

    const reservation = await reservationResponse.json();
    const nights = Math.round((new Date(checkOut) - new Date(checkIn)) / (1000*60*60*24));

    // Success
    bookingModal.hide();
    alert(
      `✅ ĐẶT PHÒNG THÀNH CÔNG!\n\n` +
      `👤 Khách hàng: ${guest.lastName} ${guest.firstName}\n` +
      `📧 Email: ${guest.email}\n` +
      `🔖 Mã đặt phòng: #${reservation.id}\n` +
      `📅 Ngày nhận: ${checkIn}  →  Ngày trả: ${checkOut} (${nights} đêm)\n` +
      `💰 Tổng tiền (dự tính): ${formatCurrency(reservation.totalAmount)}\n\n` +
      `Vui lòng lưu mã #${reservation.id} để tra cứu trạng thái đặt phòng.`
    );
    document.getElementById("guestInfoForm").reset();

    // Refresh room list to show updated availability
    document
      .getElementById("checkAvailabilityForm")
      .dispatchEvent(new Event("submit"));
  } catch (error) {
    alert("Thất bại: " + error.message);
    console.error(error);
  } finally {
    submitBtn.innerText = originalBtnText;
    submitBtn.disabled = false;
  }
}

async function trackOrder() {
  const trackingId = document.getElementById("trackingId").value;
  const resultDiv = document.getElementById("trackingResult");
  resultDiv.classList.remove("d-none");
  resultDiv.innerHTML =
    '<div class="spinner-border text-primary" role="status"></div> Đang kiểm tra...';

  if (!trackingId) {
    resultDiv.innerHTML =
      '<div class="alert alert-warning">Vui lòng nhập mã đặt phòng!</div>';
    return;
  }

  try {
    // 1. Fetch reservation
    const response = await fetch(`/api/v1/reservations/${trackingId}`);
    if (!response.ok) {
      throw new Error("Không tìm thấy đơn đặt phòng với ID này.");
    }
    const reservation = await response.json();

    // 2. Display Result
    resultDiv.innerHTML = `
        <div class="card border-success">
            <div class="card-header bg-success text-white">
                Kết Quả Tra Cứu - Mã #${reservation.id}
            </div>
            <div class="card-body">
                <h5 class="card-title">Khách hàng: ${reservation.guestName || (reservation.guest ? reservation.guest.lastName + " " + reservation.guest.firstName : "N/A")}</h5>
                <p class="card-text">
                    <strong>Email:</strong> ${reservation.guest ? reservation.guest.email : "N/A"}<br>
                    <strong>Ngày nhận:</strong> ${reservation.checkInDate}<br>
                    <strong>Ngày trả:</strong> ${reservation.checkOutDate}<br>
                    <strong>Trạng thái:</strong> <span class="badge bg-${getStatusColor(reservation.status)}">${translateStatus(reservation.status)}</span><br>
                    <strong>Tổng tiền:</strong> ${formatCurrency(reservation.totalAmount)}
                </p>
            </div>
        </div>
      `;
  } catch (error) {
    resultDiv.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
  }
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
    default:
      return "light";
  }
}

function translateStatus(status) {
  const map = {
    PENDING: "Chờ Xác Nhận",
    CONFIRMED: "Đã Xác Nhận",
    CHECKED_IN: "Đang Lưu Trú",
    CHECKED_OUT: "Đã Trả Phòng",
    CANCELED: "Đã Hủy",
  };
  return map[status] || status;
}

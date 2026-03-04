document.addEventListener("DOMContentLoaded", () => {
  loadRooms();
  loadRoomTypes();
});

let roomModal;
let roomTypesCache = [];

async function loadRoomTypes() {
  try {
    const resp = await fetch("/api/v1/rooms/types");
    if (resp.ok) {
      roomTypesCache = await resp.json();
      const select = document.getElementById("roomTypeId");
      select.innerHTML = roomTypesCache
        .map((rt) => `<option value="${rt.id}">${rt.name} ($${parseFloat(rt.basePrice).toFixed(2)}/đêm)</option>`)
        .join("");
    }
  } catch (e) {
    console.error("Failed to load room types:", e);
  }
}

function openRoomModal(room = null) {
  if (!roomModal) {
    roomModal = new bootstrap.Modal(document.getElementById("roomModal"));
  }

  if (room) {
    document.getElementById("roomId").value = room.id;
    document.getElementById("roomNumber").value = room.roomNumber;
    document.getElementById("floor").value = room.floor;
    document.getElementById("roomTypeId").value = room.roomTypeId;
    document.getElementById("status").value = room.status;
    document.getElementById("housekeepingStatus").value =
      room.housekeepingStatus;
    document.getElementById("roomModalLabel").innerText = "Cập Nhật Phòng";
  } else {
    document.getElementById("roomId").value = "";
    document.getElementById("roomNumber").value = "";
    document.getElementById("floor").value = "";
    document.getElementById("roomTypeId").value = "1";
    document.getElementById("status").value = "AVAILABLE";
    document.getElementById("housekeepingStatus").value = "CLEAN";
    document.getElementById("roomModalLabel").innerText = "Thêm Phòng Mới";
  }

  roomModal.show();
}

async function loadRooms() {
  const tableBody = document.getElementById("roomsTableBody");
  tableBody.innerHTML =
    '<tr><td colspan="7" class="text-center">Đang tải...</td></tr>';

  try {
    const response = await fetch("/api/v1/rooms");
    if (!response.ok) throw new Error("Failed to load rooms");

    const rooms = await response.json();
    renderRoomsTable(rooms);
  } catch (error) {
    console.error("Error:", error);
    tableBody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">Lỗi: ${error.message}</td></tr>`;
  }
}

function renderRoomsTable(rooms) {
  const tableBody = document.getElementById("roomsTableBody");
  if (!rooms || rooms.length === 0) {
    tableBody.innerHTML =
      '<tr><td colspan="7" class="text-center">Không có phòng nào.</td></tr>';
    return;
  }

  rooms.sort((a, b) => a.id - b.id);

  tableBody.innerHTML = rooms
    .map(
      (room) => `
        <tr>
            <td>${room.id}</td>
            <td><strong>${room.roomNumber}</strong></td>
            <td>${room.floor}</td>
            <td>${room.roomTypeName || "N/A"}</td>
            <td>${formatCurrency(room.basePrice)}</td>
            <td><span class="badge bg-${getStatusColor(room.status)}">${translateStatus(room.status)}</span></td>
            <td>
                <button class="btn btn-sm btn-info" onclick='editRoom(${JSON.stringify(room)})'><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-danger" onclick="deleteRoom(${room.id})"><i class="fas fa-trash"></i></button>
            </td>
        </tr>
    `,
    )
    .join("");
}

function editRoom(room) {
  openRoomModal(room);
}

async function saveRoom() {
  const id = document.getElementById("roomId").value;
  const roomNumber = document.getElementById("roomNumber").value;
  const floor = document.getElementById("floor").value;
  const roomTypeId = document.getElementById("roomTypeId").value;
  const status = document.getElementById("status").value;
  const housekeepingStatus =
    document.getElementById("housekeepingStatus").value;

  const data = {
    roomNumber: roomNumber,
    floor: parseInt(floor),
    roomTypeId: parseInt(roomTypeId),
    status: status,
    housekeepingStatus: housekeepingStatus,
  };

  const method = id ? "PUT" : "POST";
  const url = id ? `/api/v1/rooms/${id}` : "/api/v1/rooms";

  try {
    const response = await fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const err = await response.json();
      throw new Error(err.message || "Lưu thất bại");
    }

    alert("Lưu thành công!");
    roomModal.hide();
    loadRooms();
  } catch (error) {
    alert("Lỗi: " + error.message);
  }
}

async function deleteRoom(id) {
  if (!confirm("Bạn có chắc chắn muốn xóa phòng này không?")) return;

  try {
    const response = await fetch(`/api/v1/rooms/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) throw new Error("Xóa thất bại");

    alert("Xóa thành công!");
    loadRooms();
  } catch (error) {
    alert("Lỗi: " + error.message);
  }
}

function formatCurrency(amount) {
  if (!amount) return "$0.00";
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount);
}

function getStatusColor(status) {
  switch (status) {
    case "AVAILABLE":
      return "success";
    case "OCCUPIED":
      return "danger";
    case "RESERVED":
      return "warning";
    case "OUT_OF_SERVICE":
      return "secondary";
    default:
      return "light";
  }
}

function translateStatus(status) {
  const map = {
    AVAILABLE: "Sẵn Sàng",
    OCCUPIED: "Có Khách",
    RESERVED: "Đã Đặt",
    OUT_OF_SERVICE: "Bảo Trì",
  };
  return map[status] || status;
}

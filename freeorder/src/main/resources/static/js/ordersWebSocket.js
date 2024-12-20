let stompClient = null;

// 주문수신 웹소켓 통신 연결
function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("Connected");

        // 주문 수신
        stompClient.subscribe('/orders', function (messageOutput) {
            const order = JSON.parse(messageOutput.body);
            console.log("주문 수신")
            showOrders(order);
            updateOrderCount();
        });

    });

}

function showOrders(order) {
    const orderMessages = document.getElementById('side-order-list');
    const orderItem = document.createElement("div")
    orderItem.classList.add("order-list")
    orderItem.innerHTML = `<span class="order-icon"><a href="#">${order.orderNumber}</a></span>`

    const orderType = document.createElement("div")
    orderType.classList.add("order-type")
    if (order.type == 'HERE') {
        orderType.classList.add("here")
        orderType.innerHTML = `<i>매장</i>`
    }
    else {
        orderType.classList.add("togo")
        orderType.innerHTML = `<i>포장</i>`
    }
    orderItem.appendChild(orderType)
    const orderTitle = document.createElement("div")
    orderTitle.classList.add("title-price")
    orderTitle.innerHTML = `
        <span class="order-title"><a href="#">${order.title}</a></span>
        <span class="order-price"><a href="#">${Number(order.totalPrice).toLocaleString("ko-KR")}원</a></span>
    `
    orderItem.appendChild(orderTitle)
    const comBtn = document.createElement("button")
    comBtn.classList.add("complete-btn")
    comBtn.innerText = "주문접수"
    comBtn.addEventListener("click", () => {
        let data = {
            id: order.id,
            status: "COMPLETE"
        }
        fetch("/pos/orders", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (response.ok) {
                orderListReload()
            }
        })
    })
    const doneBtn = document.createElement("button")
    doneBtn.classList.add("done-btn")
    doneBtn.innerText = "접수완료"
    orderItem.appendChild(comBtn)
    orderItem.appendChild(doneBtn)
    orderItem.classList.add("sidebar-list")
    if (order.status == "COMPLETE") {
        orderItem.classList.add("done")
    }
    orderMessages.appendChild(orderItem);
    orderMessages.scrollTop = orderMessages.scrollHeight;
    playAlarm();
}

function orderWebSocketBinding(params) {
    document.getElementById('order-send').addEventListener('click', sendOrder);
}
connect();

// 주문 전송
function sendMessage(ordersId) {
    console.log("주문 전송 : " + ordersId)
    let data = {
        id: ordersId
    }
    if (stompClient) {
        stompClient.send("/app/order.addorder/" + ordersId, {}, JSON.stringify(data));
    }
}

// 주문 수 업데이트
function updateOrderCount() {
    fetch(`/orders/orderCount`)
        .then(response => response.json())
        .then(data => {
            document.getElementById("order-alarm").display = 'flex'
            document.getElementById('user-count').innerText = data.orderCount;
        });
}

// 영업시작 / 종료
function systemStatus(status) {
    const url = "/setting"
    const data = {
        status: status
    }
    fetch(url, {
        method: "post",
        headers: {
            "Content-Type": "application/json"
        }, body: JSON.stringify(data)
    }).then(response => {
        let data = response.json()
        if (response.ok) {
            data.then(data => {
                console.log(data.text)
                location.reload()
            })
        }
    })
}

// 🔔 알람 소리 재생
function playAlarm() {
    console.log("알림음 재생!")
    const alarmSound = document.getElementById('alarm-sound');
    alarmSound.play();
}
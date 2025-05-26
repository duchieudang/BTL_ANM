<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<meta charset="UTF-8">
<title>Chữ ký số DSA</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<script>
	function toggleForm() {
		const option = document.querySelector('input[name="signOption"]:checked').value;
		document.getElementById("textForm").style.display = (option === "text") ? "block" : "none";
		document.getElementById("fileForm").style.display = (option === "file") ? "block" : "none";
	}
	window.onload = toggleForm;

	// Hàm kiểm tra số nguyên tố
	function isPrime(num) {
		if (num < 2) return false;
		if (num === 2) return true;
		if (num % 2 === 0) return false;
		const sqrt = Math.floor(Math.sqrt(num));
		for (let i = 3; i <= sqrt; i += 2) {
			if (num % i === 0) return false;
		}
		return true;
	}

	// Sinh số nguyên tố ngẫu nhiên trong khoảng [min, max]
	function randomPrime(min, max) {
		let prime = 0;
		let attempts = 0;
		while (attempts < 10000) {
			const candidate = Math.floor(Math.random() * (max - min + 1)) + min;
			if (isPrime(candidate)) {
				prime = candidate;
				break;
			}
			attempts++;
		}
		return prime;
	}

	// Hàm tính mod lũy thừa nhanh
	function modExp(base, exp, mod) {
		let result = 1;
		base = base % mod;
		while (exp > 0) {
			if (exp % 2 === 1) result = (result * base) % mod;
			base = (base * base) % mod;
			exp = Math.floor(exp / 2);
		}
		return result;
	}

	// Hàm sinh p và q thỏa (p-1) % q = 0, p, q là nguyên tố, với q < p
	function generatePQ(minDigits = 6) {
		const min = Math.pow(10, minDigits - 1);
		const max = Math.pow(10, minDigits) * 10;
		let q, p;
		let attempts = 0;

		do {
			q = randomPrime(min, max);
			let found = false;
			for (let k = 2; k <= 10; k++) {
				const candidateP = q * k + 1;
				if (candidateP >= min && candidateP <= max && isPrime(candidateP)) {
					p = candidateP;
					found = true;
					break;
				}
			}
			attempts++;
			if (attempts > 1000) {
				alert("Không tìm được p, q thỏa mãn trong phạm vi cho phép. Vui lòng thử lại.");
				return null;
			}
		} while (!p);

		return { p, q };
	}

	// Hàm sinh x, k trong [1, q-1] và g theo công thức DSA
	function generateXGK(p, q) {
		let x = Math.floor(Math.random() * (q - 1)) + 1;
		let k = Math.floor(Math.random() * (q - 1)) + 1;

		let h = 2;
		let g = 1;
		while (h < p - 1) {
			g = modExp(h, (p - 1) / q, p);
			if (g > 1) break;
			h++;
		}

		return { x, g, k };
	}

	// Hàm tổng hợp sinh p, q, x, g, k cho form văn bản

	// Hàm tổng hợp sinh p, q, x, g, k cho form văn bản
	function generatePQXGKText() {
		const pq = generatePQ(6);
		if (!pq) return;

		const { p, q } = pq;
		const { x, g, k } = generateXGK(p, q);
		const y = modExp(g, x, p); // Khóa công khai

		document.getElementById("inputPText").value = p;
		document.getElementById("inputQText").value = q;
		document.getElementById("inputXText").value = x;
		document.getElementById("inputGText").value = g;
		document.getElementById("inputKText").value = k;
		document.getElementById("inputYText").value = y;
	}

	// Hàm tổng hợp sinh p, q, x, g, k cho form file
	function generatePQXGKFile() {
		const pq = generatePQ(6);
		if (!pq) return;

		const { p, q } = pq;
		const { x, g, k } = generateXGK(p, q);
		const y = modExp(g, x, p); // Khóa công khai

		document.getElementById("inputPFile").value = p;
		document.getElementById("inputQFile").value = q;
		document.getElementById("inputXFile").value = x;
		document.getElementById("inputGFile").value = g;
		document.getElementById("inputKFile").value = k;
		document.getElementById("inputYFile").value = y;
	}

</script>
</head>
<body class="bg-light">
	<div class="container mt-5">

		<h2 class="text-center mb-4">Hệ thống ký số DSA</h2>

		<div class="card shadow p-4 mb-4">
			<h5>Chọn hình thức ký:</h5>
			<div class="form-check">
				<input class="form-check-input" type="radio" name="signOption"
					id="optionText" value="text" onclick="toggleForm()" checked>
				<label class="form-check-label" for="optionText">Ký văn bản
					trực tiếp</label>
			</div>
			<div class="form-check">
				<input class="form-check-input" type="radio" name="signOption"
					id="optionFile" value="file" onclick="toggleForm()"> <label
					class="form-check-label" for="optionFile">Ký file Word
					(.docx), PDF (.pdf) hoặc Excel (.xlsx)</label>
			</div>
		</div>

		<!-- Form ký văn bản -->
		<div class="card shadow p-4 mb-4" id="textForm">
			<h5 class="mb-3">Nhập văn bản cần ký:</h5>
			<form action="sign" method="post">
				<div class="row mb-3">
					<div class="col-md-2">
						<label for="inputPText" class="form-label">Số nguyên tố p
							(ít nhất 6 chữ số):</label> <input type="number" name="p" id="inputPText"
							class="form-control" required minlength="6" min="100000">
					</div>
					<div class="col-md-2">
						<label for="inputQText" class="form-label">Số nguyên tố q
							sao cho (p-1) % q = 0:</label> <input type="number" name="q"
							id="inputQText" class="form-control" required minlength="6"
							min="100000">
					</div>
					<div class="col-md-2">
						<label for="inputXText" class="form-label">Số nguyên x (1
							<= x <= q-1):</label> <input type="number" name="x" id="inputXText"
							class="form-control" required min="1">
					</div>
					<div class="col-md-2">
						<label for="inputGText" class="form-label">Số nguyên g:</label> <input
							type="number" name="g" id="inputGText" class="form-control"
							required min="1"  required min="1">
					</div>
					<div class="col-md-2">
						<label for="inputYText" class="form-label">Khóa công khai
							y = g^x mod p:</label> <input type="number" name="y" id="inputYText"
							class="form-control"  required min="1">
					</div>
					<div class="col-md-2">
						<label for="inputKText" class="form-label">Số nguyên k (1
							<= k <= q-1):</label> <input type="number" name="k" id="inputKText"
							class="form-control" required min="1">
					</div>
					<div class="col-md-2 d-flex align-items-end">
						<button type="button" class="btn btn-primary w-100"
							onclick="generatePQXGKText()">Sinh ngẫu nhiên</button>
					</div>
				</div>
				<div class="mb-3">
					<textarea name="message" rows="5" class="form-control"
						placeholder="Nhập nội dung cần ký ở đây..." required></textarea>
				</div>
				<button type="submit" class="btn btn-success">Ký văn bản</button>
			</form>
		</div>

		<!-- Form ký file -->
		<div class="card shadow p-4 mb-4" id="fileForm" style="display: none;">
			<h5 class="mb-3">Chọn file cần ký:</h5>
			<form action="upload" method="post" enctype="multipart/form-data">
				<div class="row mb-3">
					<div class="col-md-2">
						<label for="inputPFile" class="form-label">Số nguyên tố p
							(ít nhất 6 chữ số):</label> <input type="number" name="p" id="inputPFile"
							class="form-control" required minlength="6" min="100000">
					</div>
					<div class="col-md-2">
						<label for="inputQFile" class="form-label">Số nguyên tố q
							sao cho (p-1) % q = 0:</label> <input type="number" name="q"
							id="inputQFile" class="form-control" required minlength="6"
							min="100000">
					</div>
					<div class="col-md-2">
						<label for="inputXFile" class="form-label">Số nguyên x (1
							<= x <= q-1)-private key)</label> <input type="number" name="x"
							id="inputXFile" class="form-control" required min="1">
					</div>
					<div class="col-md-2">
						<label for="inputGFile" class="form-label">Số nguyên g: (
							Cơ sở tạo nhóm (generator))</label> <input type="number" name="g"
							id="inputGFile" class="form-control"  required min="1">
					</div>
					<div class="col-md-2">
						<label for="inputYFile" class="form-label">Khóa công khai
							y = g^x mod p:</label> <input type="number" name="y" id="inputYFile"
							class="form-control"  required min="1">
					</div>
					<div class="col-md-2">
						<label for="inputKFile" class="form-label">Số nguyên k (1
							<= k <= q-1):</label> <input type="number" name="k" id="inputKFile"
							class="form-control" required min="1">
					</div>
					<div class="col-md-2 d-flex align-items-end">
						<button type="button" class="btn btn-primary w-100"
							onclick="generatePQXGKFile()">Sinh ngẫu nhiên</button>
					</div>
				</div>
				<div class="mb-3">
					<input type="file" name="file" accept=".docx,.pdf,.xlsx"
						class="form-control" required>
				</div>
				<button type="submit" class="btn btn-success">Ký file</button>
			</form>
		</div>
	</div>
</body>
</html>
import hashlib
import random

def hash_message(message):
    sha1 = hashlib.sha1()
    sha1.update(message.encode('utf-8'))
    return int(sha1.hexdigest(), 16)

def hash_signature(r, s):
    sha1 = hashlib.sha1()
    sha1.update((str(r) + str(s)).encode('utf-8'))
    return sha1.hexdigest()

def calculate_g(p, q, h):
    return pow(h, (p - 1) // q, p)

def generate_k(q):
    while True:
        k = random.randint(1, q - 1)
        if 1 <= k < q:
            return k

def modinv(a, m):
    # Modular inverse using extended Euclidean algorithm
    g, x, _ = extended_gcd(a, m)
    if g != 1:
        raise Exception("Không tìm được nghịch đảo modular")
    return x % m

def extended_gcd(a, b):
    if a == 0:
        return (b, 0, 1)
    else:
        g, y, x = extended_gcd(b % a, a)
        return (g, x - (b // a) * y, y)

def sign(message, p, q, g, x, k):
    r = pow(g, k, p) % q
    if r == 0:
        raise ValueError("r = 0, chọn k khác.")
    
    h = hash_message(message)
    k_inv = modinv(k, q)
    s = (k_inv * (h + x * r)) % q
    if s == 0:
        raise ValueError("s = 0, chọn k khác.")
    
    return (r, s)

def verify(message, p, q, g, y, r, s):
    if not (0 < r < q) or not (0 < s < q):
        return False
    
    w = modinv(s, q)
    h = hash_message(message)
    u1 = (h * w) % q
    u2 = (r * w) % q
    v = ((pow(g, u1, p) * pow(y, u2, p)) % p) % q
    return v == r

def verify_with_signature_hash(message, p, q, g, y, r, s, expected_signature_hash):
    if not verify(message, p, q, g, y, r, s):
        return False
    return hash_signature(r, s) == expected_signature_hash

# --- MAIN ---
if __name__ == "__main__":
    # Tham số mẫu
    p = 7879
    q = 101
    h = 2
    g = calculate_g(p, q, h)
    if g <= 1:
        raise Exception("g không hợp lệ, chọn lại h.")

    x = 45  # private key
    y = pow(g, x, p)  # public key
    k = generate_k(q)  # random k
    message = "hello DSA"

    r, s = sign(message, p, q, g, x, k)

    print("Chữ ký:")
    print("r =", r)
    print("s =", s)

    signature_hash = hash_signature(r, s)
    print("Hash của chữ ký (r||s):", signature_hash)

    valid = verify(message, p, q, g, y, r, s)
    print("Chữ ký hợp lệ (kiểu chuẩn)?", valid)

    valid_hash = verify_with_signature_hash(message, p, q, g, y, r, s, signature_hash)
    print("Chữ ký hợp lệ và trùng hash?", valid_hash)

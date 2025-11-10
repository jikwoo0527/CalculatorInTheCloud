# 🧮 Calculator Client-Server (TCP, Java)

A minimal **line-based calculator** over TCP.  
The server parses a one-line request `<op> <a> <b>` and returns a **two-line response** with a status and result or error message.

---

## ✨ Features
- ⚙️ **Multi-client server** using `ExecutorService` (fixed thread pool)  
- ➕ **Four operations:** `add`, `sub`, `mul`, `div`  
- 🧩 **Robust validation:** token count, integer parsing, operator support, divide-by-zero  
- 📡 **Simple ASCII line protocol:** one-line request, two-line response  

---

## 🗂️ Project Layout
| File | Description |
|------|--------------|
| `CalServer.java` | TCP server (port **6789** by default) |
| `CalClient.java` | Console client (reads `server_info.dat` if present) |
| `server_info.dat` | Optional config file:<br>Line 1 → IP<br>Line 2 → Port |

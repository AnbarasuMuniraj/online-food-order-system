import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [currentView, setCurrentView] = useState('place'); // 'place' or 'dashboard'
  
  // State for Place Order
  const [customerName, setCustomerName] = useState('');
  const [item, setItem] = useState('');
  const [amount, setAmount] = useState('');
  const [orderMessage, setOrderMessage] = useState(null);
  const [orderError, setOrderError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // State for Dashboard
  const [orders, setOrders] = useState([]);
  const [dashboardError, setDashboardError] = useState(null);

  // Fetch orders for dashboard
  const fetchOrders = async () => {
    try {
      const response = await axios.get('/api/orders');
      setOrders(response.data);
      setDashboardError(null);
    } catch (err) {
      console.error("Error fetching orders:", err);
      setDashboardError("Failed to fetch orders from server.");
    }
  };

  // Poll orders when view is dashboard
  useEffect(() => {
    if (currentView === 'dashboard') {
      fetchOrders();
      const interval = setInterval(fetchOrders, 2000);
      return () => clearInterval(interval);
    }
  }, [currentView]);

  // Handle Place Order Submit
  const handlePlaceOrder = async (e) => {
    e.preventDefault();
    if (!customerName || !item || !amount) {
      setOrderError("All fields are required.");
      return;
    }
    
    setSubmitting(true);
    setOrderMessage(null);
    setOrderError(null);

    try {
      const response = await axios.post('/api/orders', {
        customerName,
        item,
        amount: parseFloat(amount)
      });
      setOrderMessage(`Order placed successfully! Order ID: ${response.data.id}`);
      // Clear form
      setCustomerName('');
      setItem('');
      setAmount('');
    } catch (err) {
      console.error("Error placing order:", err);
      setOrderError("Failed to place order. Check server connection.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ fontFamily: 'sans-serif', padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h1>Online Food Order System</h1>
      
      {/* Navigation Tabs */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
        <button 
          onClick={() => setCurrentView('place')}
          style={{
            padding: '10px 20px',
            backgroundColor: currentView === 'place' ? '#007bff' : '#f8f9fa',
            color: currentView === 'place' ? '#fff' : '#000',
            border: '1px solid #ccc',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Place Order
        </button>
        <button 
          onClick={() => setCurrentView('dashboard')}
          style={{
            padding: '10px 20px',
            backgroundColor: currentView === 'dashboard' ? '#007bff' : '#f8f9fa',
            color: currentView === 'dashboard' ? '#fff' : '#000',
            border: '1px solid #ccc',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Order Dashboard
        </button>
      </div>

      {/* Place Order Page */}
      {currentView === 'place' && (
        <div style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px' }}>
          <h2>Place a New Order</h2>
          {orderMessage && <div style={{ color: 'green', marginBottom: '15px' }}>{orderMessage}</div>}
          {orderError && <div style={{ color: 'red', marginBottom: '15px' }}>{orderError}</div>}
          
          <form onSubmit={handlePlaceOrder}>
            <div style={{ marginBottom: '15px' }}>
              <label style={{ display: 'block', marginBottom: '5px' }}>Customer Name:</label>
              <input 
                type="text" 
                value={customerName} 
                onChange={(e) => setCustomerName(e.target.value)} 
                style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
                placeholder="Enter customer name"
              />
            </div>
            <div style={{ marginBottom: '15px' }}>
              <label style={{ display: 'block', marginBottom: '5px' }}>Item Name:</label>
              <input 
                type="text" 
                value={item} 
                onChange={(e) => setItem(e.target.value)} 
                style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
                placeholder="e.g. Burger, Pizza"
              />
            </div>
            <div style={{ marginBottom: '15px' }}>
              <label style={{ display: 'block', marginBottom: '5px' }}>Amount ($):</label>
              <input 
                type="number" 
                step="0.01"
                value={amount} 
                onChange={(e) => setAmount(e.target.value)} 
                style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
                placeholder="e.g. 12.99"
              />
            </div>
            <button 
              type="submit" 
              disabled={submitting}
              style={{
                padding: '10px 20px',
                backgroundColor: '#28a745',
                color: '#fff',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              {submitting ? 'Placing Order...' : 'Place Order'}
            </button>
          </form>
        </div>
      )}

      {/* Order Dashboard Page */}
      {currentView === 'dashboard' && (
        <div>
          <h2>Active Orders</h2>
          {dashboardError && <div style={{ color: 'red', marginBottom: '15px' }}>{dashboardError}</div>}
          
          <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
            <thead>
              <tr style={{ backgroundColor: '#f8f9fa', borderBottom: '2px solid #ccc' }}>
                <th style={{ textAlign: 'left', padding: '10px', borderBottom: '1px solid #ddd' }}>Order ID</th>
                <th style={{ textAlign: 'left', padding: '10px', borderBottom: '1px solid #ddd' }}>Customer Name</th>
                <th style={{ textAlign: 'left', padding: '10px', borderBottom: '1px solid #ddd' }}>Item</th>
                <th style={{ textAlign: 'left', padding: '10px', borderBottom: '1px solid #ddd' }}>Amount</th>
                <th style={{ textAlign: 'left', padding: '10px', borderBottom: '1px solid #ddd' }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {orders.length === 0 ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '20px', color: '#666' }}>
                    No orders placed yet.
                  </td>
                </tr>
              ) : (
                orders.map((order) => (
                  <tr key={order.id} style={{ borderBottom: '1px solid #eee' }}>
                    <td style={{ padding: '10px' }}>{order.id}</td>
                    <td style={{ padding: '10px' }}>{order.customerName}</td>
                    <td style={{ padding: '10px' }}>{order.item}</td>
                    <td style={{ padding: '10px' }}>${order.amount.toFixed(2)}</td>
                    <td style={{ padding: '10px' }}>
                      <span style={{
                        padding: '4px 8px',
                        borderRadius: '4px',
                        backgroundColor: 
                          order.status === 'DELIVERED' ? '#d4edda' :
                          order.status === 'CANCELLED' ? '#f8d7da' :
                          order.status === 'PLACED' ? '#fff3cd' : '#d1ecf1',
                        color:
                          order.status === 'DELIVERED' ? '#155724' :
                          order.status === 'CANCELLED' ? '#721c24' :
                          order.status === 'PLACED' ? '#856404' : '#0c5460',
                        fontWeight: 'bold',
                        fontSize: '0.85em'
                      }}>
                        {order.status}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default App;

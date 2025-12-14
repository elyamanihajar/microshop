import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function Orders() {
    const [orders, setOrders] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetch("http://localhost:8880/order-service/orders")
            .then((resp) => resp.json())
            .then((data) => setOrders(data))
            .catch((err) => console.error(err));
    }, []);

    const getStatusClass = (status) => {
        switch(status) {
            case 'PENDING': return 'status-pending';
            case 'CONFIRMED': return 'status-confirmed';
            case 'DELIVERED': return 'status-delivered';
            case 'CANCELED': return 'status-canceled';
            default: return 'status-pending';
        }
    };

    return (
        <div>
            <h2 className="page-title">Historique des Commandes</h2>
            <table className="modern-table">
                <thead>
                <tr>
                    <th>N° Commande</th>
                    <th>Date</th>
                    <th>État</th>
                    <th>Montant Total</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                {orders.length === 0 ? (
                    <tr><td colSpan="5" style={{textAlign:'center'}}>Aucune commande trouvée</td></tr>
                ) : (
                    orders.map((o) => (
                        <tr key={o.id}>
                            <td style={{fontWeight:'bold'}}>#{o.id}</td>
                            <td>{o.createdAt ? new Date(o.createdAt).toLocaleDateString() : 'N/A'}</td>
                            <td>
                                <span className={`status-badge ${getStatusClass(o.status)}`}>
                                    {o.status}
                                </span>
                            </td>
                            <td className="price-tag">{o.total.toLocaleString()} MAD</td>
                            <td style={{textAlign:'right'}}>
                                <button
                                    className="btn-pastel"
                                    onClick={() => navigate(`/orders/${o.id}`)}
                                >
                                    Voir Détails
                                </button>
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>
        </div>
    );
}

export default Orders;
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";
import CheckoutForm from "./CheckoutForm";

// --- REMPLACEZ PAR VOTRE CLE PUBLIQUE STRIPE (pk_test_...) ---
const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLIC_KEY);

function OrderDetails() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [order, setOrder] = useState(null);
    const [clientSecret, setClientSecret] = useState(""); // Pour Stripe
    const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8880";

    const fetchOrder = () => {
        fetch(`${API_URL}/order-service/fullOrder/${id}`)
            .then((resp) => resp.json())
            .then((data) => setOrder(data))
            .catch((err) => console.error(err));
    };

    useEffect(() => {
        fetchOrder();
        // Polling pour voir si le statut change (backend scheduler)
        const interval = setInterval(fetchOrder, 5000);
        return () => clearInterval(interval);
    }, [id]);

    // Quand on décide de payer, on demande le "clientSecret" au backend
    const initializePayment = () => {
        fetch(`http://localhost:8880/order-service/orders/${id}/payment-intent`, {
            method: "POST"
        })
            .then(res => res.json())
            .then(data => setClientSecret(data.clientSecret));
    };

    // Callback appelée quand Stripe a validé le paiement
    const handlePaymentSuccess = () => {
        // On dit au backend : "C'est bon, Stripe a dit oui, passe la commande en CONFIRMED"
        fetch(`http://localhost:8880/order-service/orders/${id}/pay`, {
            method: "POST"
        }).then(() => {
            fetchOrder(); // Rafraichir l'interface
            setClientSecret(""); // Fermer le formulaire
        });
    };

    const getStatusStyle = (status) => {
        switch(status) {
            case 'PENDING': return { background: '#fff3cd', color: '#856404', label: 'En attente de paiement' };
            case 'CONFIRMED': return { background: '#d1e7dd', color: '#0f5132', label: 'Payée - Préparation' };
            case 'DELIVERED': return { background: '#d4edda', color: '#155724', label: 'Livrée' };
            case 'CANCELED': return { background: '#f8d7da', color: '#721c24', label: 'Annulée' };
            default: return { label: status };
        }
    };

    if (!order) return <div className="main-container">Chargement...</div>;
    const statusInfo = getStatusStyle(order.status);

    // Options pour le look du formulaire Stripe
    const options = {
        clientSecret,
        appearance: { theme: 'stripe' }, // 'stripe' ou 'flat' ou 'night'
    };

    return (
        <div>
            <button onClick={() => navigate('/orders')} style={{background:'transparent', border:'none', color:'#636e72', marginBottom:'20px', cursor:'pointer'}}>
                ← Retour
            </button>

            <div className="modern-card">
                {/* Header Commande */}
                <div style={{display:'flex', justifyContent:'space-between', borderBottom:'1px solid #eee', paddingBottom:'20px', marginBottom:'20px'}}>
                    <div>
                        <h2 style={{margin:0}}>Commande #{order.id}</h2>
                        <span style={{color:'#b2bec3'}}>Client ID: {order.customerId}</span>
                    </div>
                    <div style={{textAlign:'right'}}>
                        <div style={{marginBottom:'5px'}}>{new Date(order.createdAt).toLocaleString()}</div>
                        <span style={{
                            padding: '5px 15px', borderRadius: '20px', fontWeight: 'bold', fontSize: '0.8rem',
                            background: statusInfo.background, color: statusInfo.color
                        }}>
                            {statusInfo.label}
                        </span>
                    </div>
                </div>

                {/* Tableau Produits */}
                <table className="modern-table">
                    <thead>
                    <tr style={{background:'#f9f9f9'}}>
                        <th>Produit</th><th>Prix</th><th>Qté</th><th style={{textAlign:'right'}}>Total</th>
                    </tr>
                    </thead>
                    <tbody>
                    {order.productItems.map((pi) => (
                        <tr key={pi.id}>
                            <td>{pi.product ? pi.product.name : "..."}</td>
                            <td>{pi.product?.price} MAD</td>
                            <td>x {pi.quantity}</td>
                            <td style={{textAlign:'right', fontWeight:'bold'}}>{pi.subTotal} MAD</td>
                        </tr>
                    ))}
                    </tbody>
                </table>

                <div style={{marginTop:'30px', textAlign:'right'}}>
                    <span style={{fontSize:'1.2rem', marginRight:'20px', color:'#636e72'}}>Total à payer</span>
                    <span style={{fontSize:'2rem', fontWeight:'bold', color:'var(--primary-dark)'}}>
                        {order.total.toLocaleString()} MAD
                    </span>
                </div>

                {/* --- ZONE DE PAIEMENT STRIPE --- */}
                {order.status === 'PENDING' && (
                    <div style={{marginTop:'40px', background:'#f8f9fa', padding:'25px', borderRadius:'12px', border:'1px solid #e9ecef'}}>
                        <h4 style={{marginBottom:'20px', color:'#2d3436'}}>💳 Paiement Sécurisé par Stripe</h4>

                        {!clientSecret ? (
                            <div style={{textAlign:'center'}}>
                                <p style={{marginBottom:'20px'}}>Cliquez ci-dessous pour initialiser le formulaire sécurisé.</p>
                                <button className="btn-pastel" onClick={initializePayment}>
                                    Procéder au paiement
                                </button>
                            </div>
                        ) : (
                            <Elements stripe={stripePromise} options={options}>
                                <CheckoutForm onSuccess={handlePaymentSuccess} />
                            </Elements>
                        )}
                    </div>
                )}

                {/* Messages de succès */}
                {order.status === 'CONFIRMED' && (
                    <div className="alert alert-success" style={{marginTop:'30px', background:'#d1e7dd', padding:'15px', borderRadius:'10px', color:'#0f5132'}}>
                        <h5>✅ Paiement Validé !</h5>
                        <p>Merci pour votre achat. Votre commande est confirmée.</p>
                    </div>
                )}
                {order.status === 'DELIVERED' && (
                    <div className="alert alert-success" style={{marginTop:'30px', background:'#cff4fc', padding:'15px', borderRadius:'10px', color:'#055160'}}>
                        <h5>🚚 Livrée</h5>
                        <p>Cette commande a été livrée.</p>
                    </div>
                )}
            </div>
        </div>
    );
}

export default OrderDetails;
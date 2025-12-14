import { useCart } from "../context/CartContext";
import { useNavigate } from "react-router-dom";

function CartSidebar() {
    const { cartItems, removeFromCart, cartTotal, isCartOpen, setIsCartOpen, clearCart } = useCart();
    const navigate = useNavigate();

    const handleCheckout = async () => {
        if(cartItems.length === 0) return;

        const orderRequest = {
            customerId: 1,
            items: cartItems.map(item => ({
                productId: item.id,
                quantity: item.quantity,
                price: item.price
            }))
        };

        try {
            const response = await fetch("http://localhost:8880/order-service/orders", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(orderRequest)
            });

            // Si le backend renvoie une erreur (ex: Stock insuffisant)
            if (!response.ok) {
                // On essaie de lire le message d'erreur du backend
                const errorData = await response.json().catch(() => ({ message: "Erreur inconnue" }));
                // Si le backend Spring Boot renvoie une trace d'erreur standard :
                const errorMessage = errorData.message || errorData.error || "Erreur lors de la validation";

                throw new Error(errorMessage);
            }

            const data = await response.json();
            alert(`Commande #${data.id} validée avec succès ! 🎉`);
            clearCart();
            setIsCartOpen(false);
            navigate("/orders");

        } catch (error) {
            console.error(error);
            // Affiche l'erreur exacte (ex: "Stock insuffisant pour le produit : King Size Bed...")
            alert("❌ Impossible de passer la commande :\n" + error.message);
        }
    };

    if (!isCartOpen) return null;

    return (
        <>
            {/* Overlay sombre derrière */}
            <div
                style={{position:'fixed', top:0, left:0, width:'100%', height:'100%', background:'rgba(0,0,0,0.3)', zIndex:1040}}
                onClick={() => setIsCartOpen(false)}
            />

            {/* Le Panier Glissant */}
            <div style={{
                position:'fixed', top:0, right:0, width:'400px', height:'100%',
                background:'white', zIndex:1050, padding:'20px',
                boxShadow:'-5px 0 15px rgba(0,0,0,0.1)', display:'flex', flexDirection:'column'
            }}>
                <div style={{display:'flex', justifyContent:'space-between', marginBottom:'20px'}}>
                    <h3>🛒 Mon Panier</h3>
                    <button onClick={() => setIsCartOpen(false)} style={{background:'none', border:'none', fontSize:'1.5rem', cursor:'pointer'}}>×</button>
                </div>

                <div style={{flex:1, overflowY:'auto'}}>
                    {cartItems.length === 0 ? (
                        <p style={{textAlign:'center', color:'#aaa', marginTop:'50px'}}>Votre panier est vide 😢</p>
                    ) : (
                        cartItems.map(item => (
                            <div key={item.id} style={{display:'flex', marginBottom:'15px', borderBottom:'1px solid #f0f0f0', paddingBottom:'10px'}}>
                                <div style={{width:'50px', height:'50px', background:'#eee', borderRadius:'8px', display:'flex', alignItems:'center', justifyContent:'center', marginRight:'10px'}}>📦</div>
                                <div style={{flex:1}}>
                                    <div style={{fontWeight:'600'}}>{item.name}</div>
                                    <div style={{color:'#888', fontSize:'0.9rem'}}>{item.price} MAD x {item.quantity}</div>
                                </div>
                                <div style={{textAlign:'right'}}>
                                    <div style={{fontWeight:'bold'}}>{item.price * item.quantity}</div>
                                    <button
                                        onClick={() => removeFromCart(item.id)}
                                        style={{color:'red', background:'none', border:'none', fontSize:'0.8rem', cursor:'pointer', textDecoration:'underline'}}
                                    >
                                        Retirer
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                <div style={{borderTop:'2px solid #f0f0f0', paddingTop:'20px'}}>
                    <div style={{display:'flex', justifyContent:'space-between', fontSize:'1.2rem', fontWeight:'bold', marginBottom:'20px'}}>
                        <span>Total</span>
                        <span>{cartTotal} MAD</span>
                    </div>
                    <button
                        className="btn-pastel"
                        style={{width:'100%', padding:'15px'}}
                        onClick={handleCheckout}
                        disabled={cartItems.length === 0}
                    >
                        Valider la commande ✅
                    </button>
                </div>
            </div>
        </>
    );
}

export default CartSidebar;
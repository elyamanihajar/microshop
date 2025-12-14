import { useEffect, useState } from "react";
import { useCart } from "../context/CartContext";

// 1. Ajoutez 'onProductSelect' dans les arguments
function ProductModal({ product, onClose, getEmoji, onProductSelect }) {
    const [recommendations, setRecommendations] = useState([]);
    const { addToCart } = useCart();

    useEffect(() => {
        if (product) {
            // On remet la liste à vide pour éviter de voir les anciennes recos pendant le chargement
            setRecommendations([]);

            fetch(`http://localhost:8880/recommendation-service/recommendations/similar/${product.id}`)
                .then(res => res.json())
                .then(data => setRecommendations(data))
                .catch(err => console.error(err));
        }
    }, [product]);

    if (!product) return null;

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
            background: 'rgba(0,0,0,0.6)', zIndex: 2000,
            display: 'flex', justifyContent: 'center', alignItems: 'center',
            backdropFilter: 'blur(5px)'
        }} onClick={onClose}>

            <div style={{
                background: 'white', width: '600px', maxWidth:'90%', borderRadius: '20px',
                padding: '30px', position: 'relative', boxShadow: '0 20px 50px rgba(0,0,0,0.3)',
                animation: 'fadeIn 0.3s'
            }} onClick={e => e.stopPropagation()}>

                <button onClick={onClose} style={{
                    position: 'absolute', top: '15px', right: '20px', border: 'none', background: 'none',
                    fontSize: '1.5rem', cursor: 'pointer', color: '#888'
                }}>×</button>

                {/* Détail du produit principal */}
                <div style={{display:'flex', gap:'20px', marginBottom:'30px'}}>
                    <div style={{fontSize:'4rem', background:'#f8f9fa', padding:'20px', borderRadius:'15px'}}>
                        {getEmoji(product)}
                    </div>
                    <div>
                        <h2 style={{margin:0}}>{product.name}</h2>
                        <span style={{background:'#eee', padding:'4px 10px', borderRadius:'10px', fontSize:'0.8rem', color:'#666'}}>
                            {product.category}
                        </span>
                        <p style={{color:'#666', margin:'10px 0', lineHeight:'1.4'}}>
                            {product.description}
                        </p>
                        <h3 style={{color:'var(--primary-dark)'}}>{product.price} MAD</h3>
                        <button
                            className="btn-pastel"
                            onClick={() => { addToCart(product, 1); onClose(); }}
                        >
                            Ajouter au panier
                        </button>
                    </div>
                </div>

                {/* Section Recommandations */}
                <h4 style={{borderTop:'1px solid #eee', paddingTop:'20px', marginBottom:'15px'}}>
                    ✨ Vous aimerez aussi
                </h4>

                {recommendations.length === 0 ? (
                    <p style={{color:'#999', fontSize:'0.9rem'}}>Chargement des suggestions...</p>
                ) : (
                    <div style={{display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(100px, 1fr))', gap:'15px'}}>
                        {recommendations.map(rec => (
                            <div key={rec.id} style={{
                                background:'#fdfbf7', padding:'10px', borderRadius:'10px', textAlign:'center',
                                border:'1px solid #eee', cursor:'pointer', transition:'transform 0.2s'
                            }}
                                // 2. MODIFICATION ICI : Au clic, on appelle la fonction du parent
                                 onClick={() => onProductSelect(rec)}
                                 onMouseOver={e => e.currentTarget.style.transform = 'scale(1.05)'}
                                 onMouseOut={e => e.currentTarget.style.transform = 'scale(1)'}
                            >
                                <div style={{fontSize:'2rem'}}>{getEmoji(rec)}</div>
                                <div style={{fontSize:'0.8rem', fontWeight:'bold', marginTop:'5px', whiteSpace:'nowrap', overflow:'hidden', textOverflow:'ellipsis'}}>
                                    {rec.name}
                                </div>
                                <div style={{fontSize:'0.8rem', color:'var(--primary-dark)'}}>
                                    {rec.price} MAD
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

export default ProductModal;
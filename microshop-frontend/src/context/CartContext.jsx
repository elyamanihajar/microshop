import { createContext, useState, useContext } from "react";

const CartContext = createContext();

export const useCart = () => useContext(CartContext);

export const CartProvider = ({ children }) => {
    const [cartItems, setCartItems] = useState([]);
    const [isCartOpen, setIsCartOpen] = useState(false);

    // Ajouter au panier
    const addToCart = (product, quantity) => {
        setCartItems(prev => {
            // Vérifier si le produit existe déjà
            const existing = prev.find(item => item.id === product.id);
            if (existing) {
                // On met à jour la quantité
                return prev.map(item =>
                    item.id === product.id
                        ? { ...item, quantity: item.quantity + parseInt(quantity) }
                        : item
                );
            }
            // Sinon on ajoute le produit complet
            return [...prev, { ...product, quantity: parseInt(quantity) }];
        });
        setIsCartOpen(true); // Ouvrir le panier automatiquement
    };

    // Supprimer du panier
    const removeFromCart = (productId) => {
        setCartItems(prev => prev.filter(item => item.id !== productId));
    };

    // Vider le panier
    const clearCart = () => setCartItems([]);

    // Calcul du total
    const cartTotal = cartItems.reduce((total, item) => total + (item.price * item.quantity), 0);

    return (
        <CartContext.Provider value={{
            cartItems,
            addToCart,
            removeFromCart,
            clearCart,
            cartTotal,
            isCartOpen,
            setIsCartOpen
        }}>
            {children}
        </CartContext.Provider>
    );
};
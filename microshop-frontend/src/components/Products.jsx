import {useEffect, useState, useCallback} from "react";
import SearchSection from "./SearchSection";
import {useCart} from "../context/CartContext";
import ProductModal from "./ProductModal";

function Products() {
    // --- ETATS ---
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [selectedProduct, setSelectedProduct] = useState(null);

    // Pagination
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [isSearching, setIsSearching] = useState(false);

    const {addToCart} = useCart();
    const [quantities, setQuantities] = useState({});

    // --- LOGIQUE INTELLIGENTE DES EMOJIS ---
    const getProductEmoji = (product) => {
        const name = product.name.toLowerCase();
        const category = product.category ? product.category.toLowerCase() : "";

        // 1. Recherche par mot clé dans le NOM (Plus précis)
        if (name.includes("laptop") || name.includes("macbook") || name.includes("pc")) return "💻";
        if (name.includes("phone") || name.includes("samsung") || name.includes("iphone") || name.includes("galaxy")) return "📱";
        if (name.includes("watch")) return "⌚";
        if (name.includes("headphone") || name.includes("sony") || name.includes("audio")) return "🎧";
        if (name.includes("mouse")) return "🖱️";
        if (name.includes("monitor") || name.includes("screen")) return "🖥️";

        if (name.includes("shirt") || name.includes("hoodie") || name.includes("top")) return "👕";
        if (name.includes("dress") || name.includes("skirt")) return "👗";
        if (name.includes("shoe") || name.includes("sneaker")) return "👟";
        if (name.includes("jacket") || name.includes("coat")) return "🧥";
        if (name.includes("belt")) return "➰";

        if (name.includes("sofa") || name.includes("couch")) return "🛋️";
        if (name.includes("bed")) return "🛏️";
        if (name.includes("lamp") || name.includes("light")) return "💡";
        if (name.includes("table") || name.includes("desk")) return "🪑";
        if (name.includes("vase")) return "🏺";

        if (name.includes("book") || name.includes("novel") || name.includes("code")) return "📚";
        if (name.includes("wallet") || name.includes("bag")) return "👜";
        if (name.includes("glasses")) return "🕶️";

        // 2. Fallback par CATEGORIE (Générique)
        if (category === "electronics") return "🔌";
        if (category === "clothing") return "👚";
        if (category === "home") return "🏠";
        if (category === "books") return "📖";
        if (category === "accessories") return "💍";

        // 3. Par défaut
        return "📦";
    };

    // --- FETCH ---
    const fetchProducts = (url, isSearchService = false, page = 0) => {
        setLoading(true);
        setError(null);

        const finalUrl = isSearchService
            ? url
            : `${url}?page=${page}&size=12`;

        fetch(finalUrl)
            .then((resp) => {
                if (!resp.ok) throw new Error("Erreur réseau");
                return resp.json();
            })
            .then((data) => {
                if (isSearchService) {
                    setProducts(data);
                    setIsSearching(true);
                } else {
                    setProducts(data._embedded ? data._embedded.products : []);
                    setTotalPages(data.page.totalPages);
                    setIsSearching(false);
                }
                setLoading(false);
            })
            .catch((err) => {
                console.error(err);
                setError("Impossible de charger les produits.");
                setLoading(false);
            });
    };

    useEffect(() => {
        if (!isSearching) {
            fetchProducts("http://localhost:8880/catalog-service/products", false, currentPage);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentPage]);

    const handleSearch = useCallback((keyword) => {
        if (!keyword || keyword.trim() === "") {
            setIsSearching(false);
            setCurrentPage(0);
            fetchProducts("http://localhost:8880/catalog-service/products", false, 0);
        } else {
            fetchProducts(`http://localhost:8880/search-service/search/name/${keyword}`, true);
        }
    }, []);

    const handleCategory = useCallback((category) => {
        if (category === "ALL") {
            setIsSearching(false);
            setCurrentPage(0);
            fetchProducts("http://localhost:8880/catalog-service/products", false, 0);
        } else {
            fetchProducts(`http://localhost:8880/search-service/search/category/${category}`, true);
        }
    }, []);

    const handleQtyChange = (id, delta) => {
        setQuantities(prev => {
            const current = prev[id] || 1;
            return {...prev, [id]: Math.max(1, current + delta)};
        });
    };

    return (
        <div>
            <h2 className="page-title">Nos Produits</h2>
            <SearchSection onSearch={handleSearch} onCategory={handleCategory}/>

            {loading && <div style={{textAlign: 'center', padding: '40px'}}>Chargement... ⏳</div>}

            {!loading && !error && products.length === 0 && (
                <div style={{textAlign: 'center', padding: '40px', color: '#888'}}>Aucun produit trouvé.</div>
            )}

            <div className="card-grid">
                {products.map((p) => {
                    const qty = quantities[p.id] || 1;
                    const isOutOfStock = p.quantity <= 0;
                    return (
                        <div className="modern-card" key={p.id} onClick={() => !isOutOfStock && setSelectedProduct(p)}
                             style={{
                                 // SI STOCK 0 : On grise la carte et on réduit l'opacité
                                 opacity: isOutOfStock ? 0.6 : 1,
                                 filter: isOutOfStock ? 'grayscale(100%)' : 'none',
                                 cursor: isOutOfStock ? 'not-allowed' : 'pointer',
                                 position: 'relative' // Pour positionner le badge "Épuisé"
                             }}>
                            {/* BADGE RUPTURE DE STOCK */}
                            {isOutOfStock && (
                                <div style={{
                                    position: 'absolute', top: '10px', right: '10px',
                                    background: '#ff7675', color: 'white',
                                    padding: '5px 10px', borderRadius: '15px',
                                    fontWeight: 'bold', fontSize: '0.8rem', zIndex: 10
                                }}>
                                    RUPTURE
                                </div>
                            )}
                            {/* ZONE EMOJI */}
                            <div style={{
                                height: '160px',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                background: '#f8f9fa', // Gris très léger
                                borderRadius: '12px',
                                marginBottom: '15px',
                                fontSize: '5rem', // EMOJI GEANT
                                userSelect: 'none'
                            }}>
                                {getProductEmoji(p)}
                            </div>

                            <div style={{display: 'flex', justifyContent: 'space-between'}}>
                                <h4>{p.name}</h4>
                                <span className="price-tag">{p.price} MAD</span>
                            </div>

                            <p style={{color: '#888', fontSize: '0.9rem', minHeight: '40px'}}>
                                {p.description ? p.description.substring(0, 50) + '...' : ''}
                            </p>

                            <div style={{
                                marginTop: '15px',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'space-between'
                            }}>
                                {/* On cache les boutons +/- si épuisé */}
                                {!isOutOfStock ? (
                                    <div style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        background: '#f0f0f0',
                                        borderRadius: '20px'
                                    }}>
                                        <button onClick={(e) => {
                                            e.stopPropagation();
                                            handleQtyChange(p.id, -1)
                                        }} style={{
                                            border: 'none',
                                            background: 'none',
                                            padding: '5px 10px',
                                            cursor: 'pointer'
                                        }}>-
                                        </button>
                                        <span style={{
                                            fontWeight: 'bold',
                                            width: '20px',
                                            textAlign: 'center'
                                        }}>{qty}</span>
                                        <button onClick={(e) => {
                                            e.stopPropagation();
                                            // Sécurité supplémentaire : on ne dépasse pas p.quantity
                                            if (qty < p.quantity) handleQtyChange(p.id, 1);
                                        }}
                                            // 2. On désactive le bouton visuellement
                                                disabled={qty >= p.quantity}
                                                style={{
                                                    border:'none', background:'none', padding:'5px 10px', fontSize:'1.2rem',
                                                    // 3. Style visuel pour montrer qu'il est bloqué
                                                    cursor: qty >= p.quantity ? 'not-allowed' : 'pointer',
                                                    opacity: qty >= p.quantity ? 0.3 : 1,
                                                    color: qty >= p.quantity ? '#aaa' : 'inherit'
                                                }}>+
                                        </button>
                                    </div>
                                ) : (
                                    <div></div> // Espace vide pour garder l'alignement
                                )}
                                <button
                                    className="btn-pastel"
                                    disabled={isOutOfStock}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        addToCart(p, qty);
                                    }}
                                    style={{
                                        background: isOutOfStock ? '#ccc' : 'var(--primary)',
                                        cursor: isOutOfStock ? 'not-allowed' : 'pointer'
                                    }}
                                >
                                    {isOutOfStock ? "Épuisé" : "Ajouter"}
                                </button>
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* Pagination */}
            {!isSearching && totalPages > 1 && (
                <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                    gap: '15px',
                    marginTop: '40px',
                    alignItems: 'center'
                }}>
                    <button
                        className="btn-pastel"
                        disabled={currentPage === 0}
                        onClick={() => setCurrentPage(prev => prev - 1)}
                        style={{opacity: currentPage === 0 ? 0.5 : 1}}
                    >
                        ← Précédent
                    </button>

                    <span style={{fontWeight: 'bold', color: 'var(--text-light)'}}>
                        Page {currentPage + 1} / {totalPages}
                    </span>

                    <button
                        className="btn-pastel"
                        disabled={currentPage + 1 >= totalPages}
                        onClick={() => setCurrentPage(prev => prev + 1)}
                        style={{opacity: currentPage + 1 >= totalPages ? 0.5 : 1}}
                    >
                        Suivant →
                    </button>
                </div>
            )}

            {/* LA MODALE */}
            {selectedProduct && (
                <ProductModal
                    product={selectedProduct}
                    onClose={() => setSelectedProduct(null)}
                    getEmoji={getProductEmoji}

                    onProductSelect={setSelectedProduct}
                />
            )}
        </div>
    );
}

export default Products;
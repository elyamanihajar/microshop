import { useState, useEffect } from "react";

function SearchSection({ onSearch, onCategory }) {
    const [searchTerm, setSearchTerm] = useState("");
    const [category, setCategory] = useState("ALL");

    const categories = ["ALL", "ELECTRONICS", "CLOTHING", "HOME", "BOOKS", "ACCESSORIES"];

    useEffect(() => {
        // On ne lance la recherche que si searchTerm n'est pas vide
        // ou si l'utilisateur a effacé manuellement (pour revenir à tout voir)
        // Mais on évite de le lancer au premier rendu si vide.
        const delayDebounceFn = setTimeout(() => {
            // Petite sécurité : on n'appelle onSearch que si ça a du sens
            if (searchTerm !== "") {
                onSearch(searchTerm);
            } else {
                // Si l'utilisateur efface tout, on veut peut-être recharger,
                // MAIS attention au conflit avec la catégorie.
                // On laisse Products.jsx gérer le cas vide via onSearch("")
                onSearch("");
            }
        }, 500);

        return () => clearTimeout(delayDebounceFn);
    }, [searchTerm, onSearch]);

    const handleCategoryChange = (e) => {
        const cat = e.target.value;
        setCategory(cat);
        onCategory(cat);

        // --- CORRECTION ICI ---
        // Ne PAS vider le searchTerm ici (setSearchTerm("")),
        // sinon cela déclenche le useEffect ci-dessus qui va
        // annuler votre filtre catégorie 500ms plus tard en rechargeant tout.

        // Si vous tenez absolument à vider le champ visuellement,
        // il faudrait une logique plus complexe dans le parent.
        // Pour l'instant, laisser le texte permet même de combiner (ex: chercher "HP" dans "ELECTRONICS" plus tard)
    };

    return (
        <div className="search-container">
            <div className="search-input-group">
                <input
                    type="text"
                    className="custom-input"
                    placeholder="🔍 Rechercher un produit..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            <div className="filter-group">
                <select className="custom-select" value={category} onChange={handleCategoryChange}>
                    {categories.map(cat => (
                        <option key={cat} value={cat}>
                            {cat === "ALL" ? "Toutes Catégories" : cat}
                        </option>
                    ))}
                </select>
            </div>
        </div>
    );
}

export default SearchSection;
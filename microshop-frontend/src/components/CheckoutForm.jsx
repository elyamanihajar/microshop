import { PaymentElement, useStripe, useElements } from "@stripe/react-stripe-js";
import { useState } from "react";

function CheckoutForm({ onSuccess }) {
    const stripe = useStripe();
    const elements = useElements();
    const [message, setMessage] = useState(null);
    const [isProcessing, setIsProcessing] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!stripe || !elements) return;

        setIsProcessing(true);

        // 1. Stripe confirme le paiement directement
        const { error, paymentIntent } = await stripe.confirmPayment({
            elements,
            redirect: "if_required", // Important pour ne pas recharger la page
        });

        if (error) {
            setMessage(error.message);
            setIsProcessing(false);
        } else if (paymentIntent && paymentIntent.status === "succeeded") {
            // 2. Si succès, on prévient le parent pour mettre à jour la BDD
            setMessage("Paiement réussi ! 🎉");
            onSuccess();
            // Le state isProcessing reste true pour empêcher de recliquer
        } else {
            setMessage("Statut inattendu.");
            setIsProcessing(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} style={{marginTop:'20px'}}>
            <PaymentElement />

            <button
                disabled={isProcessing || !stripe || !elements}
                id="submit"
                className="btn-pastel"
                style={{width:'100%', marginTop:'20px', justifyContent:'center'}}
            >
                {isProcessing ? "Traitement en cours..." : "Payer maintenant"}
            </button>

            {message && <div style={{color: message.includes('réussi') ? 'green' : 'red', marginTop:'10px', textAlign:'center'}}>{message}</div>}
        </form>
    );
}

export default CheckoutForm;
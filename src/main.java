abstract class Product {
    protected String name;
    protected double price;
    protected int stockQuantity;

    Product(String name, double price, int stockQuantity) {
        this.name = name;
        this.price = (price < 0) ? 0 : price; // Ensure price is not negative
        this.stockQuantity = (stockQuantity < 0) ? 0 : stockQuantity; // Ensure stock quantity is not negative

        if (price < 0) {
            System.out.println("Price cannot be negative. Setting price to 0.");
        }
        if (stockQuantity < 0) {
            System.out.println("Stock quantity cannot be negative. Setting stock quantity to 0.");
        }

    }

    public abstract void displayInfo();

    public void sell(int quantity) {
        if (quantity > stockQuantity) {
            System.out.println("Not enough stock to sell " + quantity + " units of " + name + ". Available stock: " + stockQuantity);
        } else {
            stockQuantity -= quantity;
            System.out.println("Sold " + quantity + " units of " + name);
        }
    }

    public void applyDiscount(double percentage) {
        if (percentage < 0 || percentage > 100) {
            System.out.println("Invalid discount percentage. Must be between 0 and 100.");
            return;
        }
        price -= price * (percentage / 100);
        System.out.println("Applied " + percentage + "% discount to " + name + ". New price: $" + price);
    }

    
}

interface Exportable {
        String exportData();
    }

class DigitalProduct extends Product implements Exportable {
    private String downloadUrl;
    private double fileSizeMb;

    DigitalProduct(String name, double price, int stockQuantity, String downloadUrl, double fileSizeMb) {
        super(name, price, stockQuantity);
        this.downloadUrl = downloadUrl;
        this.fileSizeMb = fileSizeMb;
    }

    @Override
    public void displayInfo() {
        System.out.println("DIGITAL PRODUCT: " + name);
        System.out.println("Price: $" + price);
        System.out.println("Stock Quantity: " + stockQuantity);
        System.out.println("Download URL: " + downloadUrl);
        System.out.println("File Size: " + fileSizeMb + " MB");
    }

    @Override
    public String exportData() {
        return "DIGITAL | " + name + ", Price: $" + price + ", Stock: " + stockQuantity + ", Download URL: " + downloadUrl + ", File Size: " + fileSizeMb + " MB";
    }
}

class PhysicalProduct extends Product implements Exportable {
    private double weightKg;
    private String shippingRegion;
    

    PhysicalProduct(String name, double price, int stockQuantity, double weightKg, String shippingRegion) {
        super(name, price, stockQuantity);
        this.weightKg = weightKg;
        this.shippingRegion = shippingRegion;
    }

    @Override
    public void displayInfo() {
        System.out.println("PHYSICAL PRODUCT: " + name);
        System.out.println("Price: $" + price);
        System.out.println("Stock Quantity: " + stockQuantity);
        System.out.println("Weight: " + weightKg + " kg");
        System.out.println("Shipping Region: " + shippingRegion);
    }
}


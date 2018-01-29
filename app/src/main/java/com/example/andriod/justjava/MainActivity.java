package com.example.andriod.justjava;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity {

    int quantity;
    LinearLayout mainLayout;
    int wishesIndex;
    Button orderButton;
    EditText wishesEditText;
    ArrayList<ViewGroup> listOfCoffees;

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ViewGroup parentView = (ViewGroup) v.getParent();
            mainLayout.removeView(parentView);
            listOfCoffees.remove(listOfCoffees.indexOf(parentView));
            quantity--;
            display(quantity);
            for (int index = 0; index < listOfCoffees.size(); index++) {
                TextView numberCoffee = (TextView) listOfCoffees.get(index).getChildAt(0);
                numberCoffee.setText(getString(R.string.coffee, index + 1));
            }
        }
    };

    View.OnClickListener onOrderButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            submitOrder();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wishesEditText = findViewById(R.id.wishes);
        orderButton = findViewById(R.id.order_button);
        orderButton.setOnClickListener(onOrderButtonListener);
        mainLayout = findViewById(R.id.main_layout);
        listOfCoffees = new ArrayList<>();
        ArrayList<Integer> chocolate = new ArrayList<>();
        ArrayList<Integer> whippedCream = new ArrayList<>();
        if (savedInstanceState != null) {
            quantity = savedInstanceState.getInt("quantity");
            chocolate = savedInstanceState.getIntegerArrayList("chocolate");
            whippedCream = savedInstanceState.getIntegerArrayList("whippedCream");
        } else {
            quantity = 2;
        }
        display(quantity);
        // add topping selection layout
        for (int coffees = 1; coffees <= quantity; coffees++) {
            addToppings();
        }
        if (chocolate.size() != 0 && whippedCream.size() != 0) {
            for (int coffeeNumber = 0; coffeeNumber < listOfCoffees.size(); coffeeNumber++) {
                CheckBox temp = listOfCoffees.get(coffeeNumber).findViewById(R.id.checkbox_chocolate);
                if (chocolate.get(coffeeNumber) == 1) {
                    temp.setChecked(true);
                }
                temp = listOfCoffees.get(coffeeNumber).findViewById(R.id.checkbox_whippedCream);
                if (whippedCream.get(coffeeNumber) == 1) {
                    temp.setChecked(true);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("quantity", quantity);
        ArrayList<Integer> whippedCream = new ArrayList<>();
        ArrayList<Integer> chocolate = new ArrayList<>();
        for (ViewGroup coffeeNumber : listOfCoffees) {
            CheckBox temp = (CheckBox) coffeeNumber.findViewById(R.id.checkbox_chocolate);
            chocolate.add(temp.isChecked() ? 1 : 0);
            temp = (CheckBox) coffeeNumber.findViewById(R.id.checkbox_whippedCream);
            whippedCream.add(temp.isChecked() ? 1 : 0);
        }
        savedInstanceState.putIntegerArrayList("whippedCream", whippedCream);
        savedInstanceState.putIntegerArrayList("chocolate", chocolate);
    }

    // this method displays the given quantity value on the screen
    private void display(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        String orderedNumber = String.valueOf(number);
        quantityTextView.setText(orderedNumber);
    }

    /**
     * this method adds a new toppingLayout if number of coffees is increased
     */
    private void addToppings() {
        int newId;
        // generate unique View Id for toppingLayout
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newId = ViewIdGenerator.generateViewId();
        } else {
            newId = View.generateViewId();
        }

        LinearLayout toppingLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.toppings, null);
        TextView textCoffeeNumber = (TextView) toppingLayout.getChildAt(0);
        TextView deleteButton = (TextView) toppingLayout.getChildAt(1);
        deleteButton.setTypeface(FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME));
        deleteButton.setText(R.string.delete);
        deleteButton.setOnClickListener(onClickListener);
        textCoffeeNumber.setText(getString(R.string.coffee, listOfCoffees.size() + 1));
        wishesIndex = mainLayout.indexOfChild(wishesEditText);
        toppingLayout.setId(newId);
        mainLayout.addView(toppingLayout, wishesIndex);
        listOfCoffees.add(toppingLayout);

    }

    /**
     * this method remove a last toppingLayout if number of coffees is decreases
     */
    private void removeTopping() {
        wishesIndex = mainLayout.indexOfChild(wishesEditText);
        mainLayout.removeViewAt(wishesIndex - 1);
        listOfCoffees.remove(listOfCoffees.size() - 1);
    }

    /**
     * increment the ordered quantity by clicking the +Button
     *
     * @param view
     */
    public void increment(View view) {
        if (quantity <= 100) {
            quantity++;
        } else {
            CharSequence upperBound = getString(R.string.more_than_100);
            Toast.makeText(this, upperBound, Toast.LENGTH_SHORT).show();
            return;
        }
        display(quantity);
        addToppings();
    }

    /**
     * decrement the ordered quantity by clicking the -Button
     *
     * @param view
     */
    public void decrement(View view) {
        if (quantity <= 1) {
            CharSequence lowerBound = getString(R.string.less_than_1);
            Toast.makeText(this, lowerBound, Toast.LENGTH_SHORT).show();
            return;
        } else {
            quantity--;
        }
        display(quantity);
        removeTopping();
    }

    /**
     * calculates the prices depending on the quantity
     *
     * @param orderedQuantity is the quantity which was ordered,
     * @param orderedCoffees  array for the different coffee types and number and price
     * @return total price
     */
    private int calculatePrice(int orderedQuantity, int[][] orderedCoffees) {
        int price = 0;
        for (int[] differentCoffees : orderedCoffees) {
            int pricePerCup = 5;
            if (differentCoffees[2] == 1) {
                pricePerCup += 2;
            }
            if (differentCoffees[3] == 1) {
                pricePerCup += 1;
            }
            price += pricePerCup * differentCoffees[0];
            differentCoffees[1] = pricePerCup;
        }
        return price;
    }

    /**
     * Get name from EditText-View
     *
     * @return String of the name, inputted in teh EditText customer_name
     */
    private String getCustomerName() {
        EditText editTextName = (EditText) findViewById(R.id.customer_name);
        return editTextName.getText().toString();
    }

    /**
     * Set boolean whippedCreamOrdered to true, when checkbox is checked
     */
    private boolean orderWhippedCream(ViewGroup currentCoffee) {
        CheckBox whippedCreamCheckBox = (CheckBox) currentCoffee.findViewById(R.id.checkbox_whippedCream);
        return whippedCreamCheckBox.isChecked();
    }

    /**
     * Set boolean whippedCreamOrdered to true, when checkbox is checked
     */
    private boolean orderChocolate(ViewGroup currentCoffee) {
        CheckBox chocolateCheckBox = (CheckBox) currentCoffee.findViewById(R.id.checkbox_chocolate);
        return chocolateCheckBox.isChecked();
    }

    // This method is called when order button is clicked
    public void submitOrder() {
        // array with different toppings -> number, price, chocolate, whipped cream
        int[][] orderedCoffees = new int[4][4];
        int numDifCoffees = 0;
        for (ViewGroup currentCoffee : listOfCoffees) {
            int hasWhippedCream = orderWhippedCream(currentCoffee) ? 1 : 0;
            int hasChocolate = orderChocolate(currentCoffee) ? 1 : 0;
            boolean addToOrder = false;
            for (int[] differentCoffees : orderedCoffees) {
                if (differentCoffees[2] == hasChocolate && differentCoffees[3] == hasWhippedCream) {
                    differentCoffees[0]++;
                    addToOrder = true;
                    break;
                }
            }
            if (!addToOrder) {
                orderedCoffees[numDifCoffees][0]++;
                orderedCoffees[numDifCoffees][2] = hasChocolate;
                orderedCoffees[numDifCoffees][3] = hasWhippedCream;
                numDifCoffees++;
            }
        }
        String customerName = getCustomerName();
        int price = calculatePrice(quantity, orderedCoffees);
        String orderMessage = createOrderSummary(price, orderedCoffees, customerName);
        sendEmail(orderMessage, customerName);
        // displayMessage(orderMessage);
    }

    /**
     * Displays the summary of the order (name of custumor, ordered quantity, total price)
     *
     * @param priceOfOrder   price of ordered coffees
     * @param orderedCoffees array for the differnt coffee types
     * @return order Message with all parameters filled in
     */
    private String createOrderSummary(int priceOfOrder, int[][] orderedCoffees, String customerName) {
        StringBuilder message = new StringBuilder(getString(R.string.customer_name, customerName));
        for (int[] differentCoffees : orderedCoffees) {
            if (differentCoffees[0] != 0) {
                message.append("\n");
                message.append(getString(R.string.multiple_coffees, differentCoffees[0]));
                message.append("    ");
                message.append(getString(R.string.add_whipped_cream, differentCoffees[3] >= 1));
                message.append("    ");
                message.append(getString(R.string.add_chocolate, differentCoffees[2] >= 1));
                message.append("    ");
                message.append(getString(R.string.price_per_cup, differentCoffees[1]));
            }
        }
        message.append("\n").append(getString(R.string.ordered_quantity, listOfCoffees.size()));
        message.append("\n").append(getString(R.string.total, NumberFormat.getCurrencyInstance().format(priceOfOrder)));

        if (!wishesEditText.getText().toString().isEmpty()) {
            message.append("\n").append(getString(R.string.your_wishes, wishesEditText.getText()));
        }
        message.append("\n").append(getString(R.string.thank_you));
        return message.toString();
    }

    private void sendEmail(String orderMessage, String name) {
        Intent callMailApp = new Intent(Intent.ACTION_SEND);
        //callMailApp.setData(Uri.parse("mailto:"));
        callMailApp.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.coffe_order, name));
        callMailApp.putExtra(Intent.EXTRA_TEXT, orderMessage);
        callMailApp.setType("text/plain");
        /*if (callMailApp.resolveActivity(getPackageManager()) != null) {
            startActivity(callMailApp);
        }*/

        // Always use string resources for UI text.
// This says something like "Share this photo with"
        String title = getResources().getString(R.string.chooser_title);
// Create intent to show the chooser dialog
        Intent chooser = Intent.createChooser(callMailApp, title);

// Verify the original intent will resolve to at least one activity
        if (callMailApp.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }

    }

    //this method display the message of the price
   /* private void displayMessage(String message) {
        TextView orderSummaryTextView = (TextView) findViewById(R.id.order_summary_text_view);
        orderSummaryTextView.setText(message);
    }*/
}

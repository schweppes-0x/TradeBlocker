import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionInfo;
import gearth.extensions.extra.tools.PacketInfoSupport;
import gearth.protocol.HMessage;
import gearth.services.packet_info.PacketInfoManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import gearth.extensions.ExtensionFormLauncher;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.Objects;


@ExtensionInfo(
        Title = "TradeBlocker",
        Description = "Blocks incoming (spam)trades",
        Version = "1.0",
        Author = "-MrTn-"
)

public class TradeBlocker extends ExtensionForm {
    private PacketInfoSupport packetInfoSupport;

    private static final Color redColor = Color.rgb(240,128,128);
    private static final Color greenColor = Color.rgb(132,193,100);

    public Label stateLbl;
    public Label totalBlockedLbl;
    public CheckBox alwaysTopChk;
    public ToggleButton toggleBtn;

    private int totalBlocked = 0;

    public static void main(String[] args) {
        ExtensionFormLauncher.trigger(TradeBlocker.class, args);
    }

    @Override
    public ExtensionForm launchForm(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(TradeBlocker.class.getClassLoader().getResource("AntiSpamTrade.fxml"));
        Parent root = loader.load();

        stage.setTitle("My Extension");
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResource("schweppes.png")).openStream()));
        stage.setResizable(false);
        return loader.getController();
    }

    @Override
    protected void initExtension() {
        packetInfoSupport = new PacketInfoSupport(this);
        packetInfoSupport.intercept(HMessage.Direction.TOCLIENT, "TradingOpen", this::onIncomingTrade);
    }

    private void onIncomingTrade(HMessage hMessage) {

        if(toggleBtn.isSelected()){
            hMessage.setBlocked(true);
            packetInfoSupport.sendToServer("CloseTrading");
            Platform.runLater(() -> {
                totalBlockedLbl.textProperty().setValue("Total trades blocked: " + ++totalBlocked);
            });
        }
    }

    public void changeState(ActionEvent actionEvent) {
        if(toggleBtn.isSelected()){
            //is turned on

            stateLbl.setBackground(new Background(new BackgroundFill(greenColor, CornerRadii.EMPTY, Insets.EMPTY)));
            stateLbl.textProperty().setValue("TradeBlocker is ON");

        }else {
            //is turned off

            stateLbl.setBackground(new Background(new BackgroundFill(redColor, CornerRadii.EMPTY, Insets.EMPTY)));
            stateLbl.textProperty().setValue("TradeBlocker is OFF");

        }

    }

    public void changeAlwaysOnTop(ActionEvent actionEvent) {
        primaryStage.setAlwaysOnTop(alwaysTopChk.isSelected());
    }
}
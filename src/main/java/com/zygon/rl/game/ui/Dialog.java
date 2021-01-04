package com.zygon.rl.game.ui;

import org.hexworks.zircon.api.builder.component.ModalBuilder;
import org.hexworks.zircon.api.component.Container;
import org.hexworks.zircon.api.component.modal.Modal;
import org.hexworks.zircon.api.component.modal.ModalFragment;
import org.hexworks.zircon.api.component.modal.ModalResult;
import org.hexworks.zircon.api.screen.Screen;
import org.hexworks.zircon.api.uievent.KeyCode;
import org.hexworks.zircon.api.uievent.KeyboardEventType;
import org.hexworks.zircon.api.uievent.UIEventResponse;
import org.hexworks.zircon.internal.component.modal.EmptyModalResult;

/**
 *
 * @author zygon
 */
final class Dialog implements ModalFragment<ModalResult> {

    private final Screen screen;
    private final Container container;

    public Dialog(Screen screen, Container container) {
        this.screen = screen;
        this.container = container;
    }

    @Override
    public Modal<ModalResult> getRoot() {
        Modal<ModalResult> modal = ModalBuilder.newBuilder().withComponent(container).withParentSize(screen.getSize()).build();
        modal.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED, (event, phase) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                modal.close(EmptyModalResult.INSTANCE);
                return UIEventResponse.processed();
            } else {
                return UIEventResponse.pass();
            }
        });
        return modal;
    }

}

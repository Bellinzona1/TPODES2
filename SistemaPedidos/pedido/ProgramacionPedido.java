package pedido;

import java.util.Date;

public class ProgramacionPedido {
    private Date programadoPara;

    public void programarPara(Date fechaHora) {
        this.programadoPara = fechaHora;
    }

    public Date getProgramadoPara() {
        return programadoPara;
    }
}


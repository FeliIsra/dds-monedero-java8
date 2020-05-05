package dds.monedero.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();
  private Object Deposito;
  private Object Extraccion;

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double monto) {
    if (montoValido(monto)) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }

    if (!sePuedeDepositar()) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }

    Deposito deposito  = new Deposito(LocalDate.now(), monto);
    movimientos.add(deposito);
    setSaldo(deposito.realizarSobre(saldo));
  }

  public void sacar(double monto) {
    if (montoValido(monto)) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
    if (puedeExtraer(monto)) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }

    if (superaLimite(monto)) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite());
    }

    Extraccion extraccion  = new Extraccion(LocalDate.now(), monto);
    movimientos.add(extraccion);
    setSaldo(extraccion.realizarSobre(saldo));
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.getClass().equals(Extraccion) && movimiento.esDeLaFecha(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  private boolean montoValido(double monto){ return monto < 0; }

  private boolean sePuedeDepositar(){
    return getMovimientos().stream()
            .filter(movimiento -> movimiento.getClass().equals(Deposito))
            .count() >= 3;
  }

  private boolean puedeExtraer(double monto){ return getSaldo() - monto < 0; }

  private double limite(){
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    return  1000 - montoExtraidoHoy;
  }

  private  boolean superaLimite(double monto){ return monto > limite(); }
}

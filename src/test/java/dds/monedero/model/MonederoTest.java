package dds.monedero.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

public class MonederoTest {
  private Monedero monedero;

  @Before
  public void init() {
    monedero = new Monedero();
  }

  @Test
  public void Poner() {
    monedero.poner(1500);
    Assert.assertEquals(1500, monedero.getSaldo(), 0);
  }

  @Test(expected = MontoNegativoException.class)
  public void PonerMontoNegativo() {
    monedero.poner(-1500);
  }

  @Test
  public void TresDepositos() {
    monedero.poner(1500);
    monedero.poner(456);
    monedero.poner(1900);
    Assert.assertEquals(3, monedero.getMovimientos().size());
  }

  @Test(expected = MaximaCantidadDepositosException.class)
  public void MasDeTresDepositos() {
    monedero.poner(1500);
    monedero.poner(456);
    monedero.poner(1900);
    monedero.poner(245);
  }

  @Test(expected = SaldoMenorException.class)
  public void ExtraerMasQueElSaldo() {
    monedero.setSaldo(90);
    monedero.sacar(1001);
  }

  @Test(expected = MaximoExtraccionDiarioException.class)
  public void ExtraerMasDe1000() {
    monedero.setSaldo(5000);
    monedero.sacar(1001);
  }

  @Test(expected = MontoNegativoException.class)
  public void ExtraerMontoNegativo() {
    monedero.sacar(-500);
  }

  

}
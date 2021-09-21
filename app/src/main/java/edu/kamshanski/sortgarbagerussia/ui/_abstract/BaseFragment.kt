package edu.kamshanski.tpuclassschedule.activities._abstract


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import edu.kamshanski.tpuclassschedule.utils.lg
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Основа фрагмента с соответствующим объектом [ViewBinding]. Для его создания используется рефлексия.
 * Раньше тут был [ViewModel], но теперь он заменён на Котлиновские расширения.
 *
 * Поочереди позволяет
 *  - сконфигурировать View'шки,
 *  - подписаться на [ViewModel],
 *  - установить Listener'ы на  View'шки.
 */
abstract class BaseFragment : Fragment() {
    // Methods to implement
    /** Non ui or viewmodel activity */
    protected open fun initFragment() {}
    /** сконфигурировать View'шки: начальные значения, размеры, цвета, картинки, создание объектов программно */
    protected open fun initViews() {}
    /** подписаться на [ViewModel]: усановить [LiveData.observe], записать начальные значения туда же*/
    protected open fun initViewModel() {}
    /** установить Listener'ы на  View'шки*/
    protected open fun initListeners() {}

    // Trigger the flow and start listening for values.
    // This happens when lifecycle is STARTED and stops
    // collecting when the lifecycle is STOPPED
    inline fun repeat(crossinline block: suspend () -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                block()
            }
        }
    }

    // One-shot async actions
    inline fun launch(crossinline block: suspend () -> Unit) {
        lifecycleScope.launch {
            block()
            cancel()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lg("Attach " + this::class.java.simpleName + " to " + context.toString())
        initFragment()
    }

    // Base


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)
        //initViewModel<ViewModel>()    // для создания ViewModel с помощью рефлексии
        initViews()
        initViewModel()
        initListeners()
    }


    private lateinit var inflate: Method
    private lateinit var bindingField: Field
//    private lateinit var viewModelField: Field

    init {
        var foundBinding = false
//        var foundViewModel = false
        var clazz: Class<*> = this.javaClass
        while (!foundBinding && clazz != Any::class.java) {
            for (declaredField in clazz.declaredFields) {
                if (ViewBinding::class.java.isAssignableFrom(declaredField.type)) {
                    bindingField = declaredField
                    bindingField.isAccessible = true
                    for (method in bindingField.type.methods) {
                        if (method.parameterTypes.size == 3) {
                            inflate = method
                            foundBinding = true
                            break
                        }
                    }
//            } else if (ViewModel::class.java.isAssignableFrom(declaredField.type)) {
//                viewModelField = declaredField
//                viewModelField.isAccessible = true
//                foundViewModel = true
                }

                if (foundBinding
//                && foundViewModel
                ) {
                    break
                }
            }
            clazz = clazz.superclass
        }
    }



// Позволяет создавать ViewModel с помощью рефлексии
//    @SuppressWarnings("unchecked")
//    private fun <T : ViewModel> initViewModel() {
//        try {
//            val vm = ViewModelProvider(this).get(viewModelField.type as Class<T>);
//            viewModelField.set(this, vm);
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) : View? {
        try {
            var binding = bindingField[this] as ViewBinding?
            if (binding == null) {
                binding = inflate.invoke(null, inflater, container, false) as ViewBinding
                bindingField[this] = binding
            }
            return binding.root
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        try {
            bindingField[this] = null
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        super.onDestroyView()
//        requireActivity().onBackPressedDispatcher.addCallback(this) {
//            val navController = findNavController()
//            if (navController.currentBackStackEntry?.destination?.id != null) {
//                findNavController().popBackStackAllInstances(
//                    navController.currentBackStackEntry?.destination?.id!!,
//                    true
//                )
//            } else
//                navController.popBackStack()
//        }
    }
}


package com.gokanaz.gallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import com.gokanaz.gallery.R
import com.gokanaz.gallery.databinding.FragmentSettingsBinding
import com.gokanaz.gallery.utils.Constants
import com.gokanaz.gallery.viewmodels.GalleryViewModel

class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: GalleryViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupSettingsList()
    }
    
    private fun setupSettingsList() {
        val settingsItems = listOf(
            SettingsItem(
                title = "Theme",
                summary = "Change app theme",
                type = SettingsType.THEME
            ),
            SettingsItem(
                title = "Sort by",
                summary = "Date, Name, or Size",
                type = SettingsType.SORT
            ),
            SettingsItem(
                title = "Sort order",
                summary = "Ascending or Descending",
                type = SettingsType.ORDER
            ),
            SettingsItem(
                title = "About",
                summary = "Kanaz Gallery v1.0",
                type = SettingsType.ABOUT
            )
        )
        
        // Setup recycler view for settings
        binding.settingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.settingsRecyclerView.adapter = SettingsAdapter(settingsItems)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    enum class SettingsType {
        THEME, SORT, ORDER, ABOUT
    }
    
    data class SettingsItem(
        val title: String,
        val summary: String,
        val type: SettingsType
    )
    
    inner class SettingsAdapter(private val items: List<SettingsItem>) : 
        RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_settings, parent, false)
            return ViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }
        
        override fun getItemCount(): Int = items.size
        
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val title: MaterialTextView = itemView.findViewById(R.id.settingsTitle)
            private val summary: MaterialTextView = itemView.findViewById(R.id.settingsSummary)
            
            fun bind(item: SettingsItem) {
                title.text = item.title
                summary.text = item.summary
                
                itemView.setOnClickListener {
                    when (item.type) {
                        SettingsType.THEME -> showThemeDialog()
                        SettingsType.SORT -> showSortDialog()
                        SettingsType.ORDER -> showOrderDialog()
                        SettingsType.ABOUT -> showAboutDialog()
                    }
                }
            }
        }
    }
    
    private fun showThemeDialog() {
        val currentTheme = viewModel.getThemeMode()
        val items = Constants.ThemeMode.values().map { it.name }
        val checkedItem = currentTheme.ordinal
        
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Theme")
            .setSingleChoiceItems(items.toTypedArray(), checkedItem) { dialog, which ->
                val selected = Constants.ThemeMode.values()[which]
                viewModel.setThemeMode(selected)
                dialog.dismiss()
                // Apply theme
                requireActivity().recreate()
            }
            .show()
    }
    
    private fun showSortDialog() {
        val currentSort = viewModel.getSortType()
        val items = Constants.SortType.values().map { it.name }
        val checkedItem = currentSort.ordinal
        
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sort by")
            .setSingleChoiceItems(items.toTypedArray(), checkedItem) { dialog, which ->
                val selected = Constants.SortType.values()[which]
                viewModel.setSortType(selected)
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showOrderDialog() {
        val currentOrder = viewModel.getSortOrder()
        val items = Constants.SortOrder.values().map { it.name }
        val checkedItem = currentOrder.ordinal
        
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sort order")
            .setSingleChoiceItems(items.toTypedArray(), checkedItem) { dialog, which ->
                val selected = Constants.SortOrder.values()[which]
                viewModel.setSortOrder(selected)
                dialog.dismiss()
            }
            .show()
    }
    
    private fun showAboutDialog() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Kanaz Gallery")
            .setMessage("Version 1.0\n\nDeveloped by GoKanaz\n\nModern gallery app with Material Design")
            .setPositiveButton("OK", null)
            .show()
    }
}
